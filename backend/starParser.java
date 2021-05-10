import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class starParser extends DefaultHandler {
    // XML
    private String tempVal;
    private Actor tempActor;
    private String tempName;
    // XML RESULT
    private ArrayList<Actor> parsedActors;
    // DB CACHE
    private HashSet<Actor> dbActors;
    private int maxActorID;
    // DB CONNECTION
    private Connection connection;


    public starParser() throws Exception {
        // DB CONNECTION
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        // XML
        this.parsedActors = new ArrayList<>();
        // DB CACHE
        initDB();
    }

    private void initDB() throws SQLException {
        dbActors = new HashSet<Actor>();
        Statement statement = connection.createStatement();
        // STAR
        String starQuery = "SELECT * FROM stars";
        ResultSet starSet = statement.executeQuery(starQuery);
        while (starSet.next()) {
            String starName = starSet.getString("name");
            String starDOB = starSet.getString("birthYear");
            dbActors.add(new Actor(starName, starDOB));
        }
        starSet.close();
        // STAR ID
        String starIDQuery = "SELECT MAX(id) FROM stars";
        ResultSet starIDSet = statement.executeQuery(starIDQuery);
        while (starIDSet.next()) {
            String starID = starIDSet.getString("MAX(id)");
            maxActorID = Integer.parseInt(starID.substring(2));
        }
        starIDSet.close();
        statement.close();

    }

    public void run() throws SQLException {
        String actorsDocument = "actors63.xml";
        parseDocument(actorsDocument);
        insertActors();
    }

    private void parseDocument(String document) {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            //parse the file and also register this class for call backs
            sp.parse(document, this);
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private void insertActors() {
        PreparedStatement actorStatement = null;
        String actorInsert = "INSERT INTO stars VALUES(?, ?, ?)";

        try {
            connection.setAutoCommit(false);
            actorStatement = connection.prepareStatement(actorInsert);
            for (Actor actor: parsedActors) {
                String name = actor.getName();
                String dob = actor.getBirthYear();
                if (dbActors.contains(actor)) {
                    System.out.printf("Error: stars(name: %s, birthYear: %s) exists in DB.\n", name, dob);
                } else {
                    dbActors.add(actor);
                    maxActorID++;
                    actorStatement.setString(1, "nm" + maxActorID);
                    actorStatement.setString(2, name);
                    actorStatement.setString(3, dob);
                    actorStatement.addBatch();
                }
            }
            actorStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
            actorStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            tempActor = new Actor();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            parsedActors.add(tempActor);
            tempName = null;
        } else if (qName.equalsIgnoreCase("stagename")) {
            tempActor.setName(tempVal);
            tempName = tempVal;
        } else if (qName.equalsIgnoreCase("dob")) {
            if (tempVal.isEmpty()) {
                tempVal = null;
            }
            try {
                Integer.parseInt(tempVal);
            } catch (Exception e) {
                System.out.printf("Inconsistency: %s has 'dob' field with non-integer value.\n", tempName);
                tempVal = null;
            }
            tempActor.setBirthYear(tempVal);
        }
    }

    public static void main(String[] args) throws Exception {
        starParser spe = new starParser();
        spe.run();
    }
}