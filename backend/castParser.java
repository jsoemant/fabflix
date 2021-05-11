import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class castParser extends DefaultHandler {
    // XML
    private String tempVal;
    private StarMovie tempStarMovie;
    private String tempDirector;
    // XML RESULT
    private HashSet<StarMovie> parsedStarMovie;
    private HashMap<String, String> dbActors;
    private HashMap<String, HashSet<String>> dbSiM;

    private HashMap<Movie, String> dbMovies;
    private int maxActorID;
    // DB CACHE
    // DB CONNECTION
    private Connection connection;
    private boolean add;


    public castParser() throws Exception {
        // DB CONNECTION
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        // XML
        parsedStarMovie = new HashSet<StarMovie>();
        // DB CACHE
        initDB();
    }

    private void initDB() throws SQLException {
        dbActors = new HashMap<String, String>();
        dbMovies = new HashMap<Movie, String>();
        dbSiM = new HashMap<String, HashSet<String>>();
        Statement statement = connection.createStatement();
        // STAR
        String starQuery = "SELECT * FROM stars WHERE birthYear is null";
        ResultSet starSet = statement.executeQuery(starQuery);
        while (starSet.next()) {
            String starName = starSet.getString("name");
            String id = starSet.getString("id");
            dbActors.put(starName, id);
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

        // MOVIES
        String movieQuery = "SELECT * FROM movies M GROUP BY M.title";
        ResultSet movieSet = statement.executeQuery(movieQuery);
        while (movieSet.next()) {
            String movieTitle = movieSet.getString("title");
            String movieDirector = movieSet.getString("director");
            String movieId = movieSet.getString("id");

            Movie temp = new Movie(movieTitle, movieDirector, null);

            dbMovies.put(temp, movieId);
        }
        movieSet.close();

        // STAR IN MOVIES
        String simQuery = "SELECT * FROM stars_in_movies";
        ResultSet simSet = statement.executeQuery(simQuery);
        while (simSet.next()) {
            String starId = simSet.getString("starId");
            String movieId = simSet.getString("movieId");

            HashSet<String> tmp;
            if (dbSiM.containsKey(movieId)) {
                tmp = dbSiM.get(movieId);
            } else {
                tmp = new HashSet<String>();
            }

            tmp.add(starId);
            dbSiM.put(movieId, tmp);
        }
        simSet.close();

        statement.close();
    }

    public void run() throws SQLException {
        String actorsDocument = "casts124.xml";
        long start = System.currentTimeMillis();
        parseDocument(actorsDocument);
        insertActors();
        long end = System.currentTimeMillis();
//        System.out.println("Time in Seconds for Cast Parser: " + ((end - start) / 1000.0));
        System.out.println("Time in Seconds for Cast Parser: " + (end - start)/1000.0);
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
        PreparedStatement actorMovieStatement = null;
        String actorInsert = "INSERT INTO stars VALUES(?, ?, ?)";
        String actorMovieInsert = "INSERT INTO stars_in_movies VALUES(?, ?)";

        try {
            connection.setAutoCommit(false);
            actorStatement = connection.prepareStatement(actorInsert);
            actorMovieStatement = connection.prepareStatement(actorMovieInsert);
            for (StarMovie starmovie: parsedStarMovie) {
                String star = starmovie.getStar();
                String director = starmovie.getDirector();
                String movie = starmovie.getMovie();

                if (!dbActors.containsKey(star)) {
                    maxActorID++;
                    String actorId = "nm" + maxActorID;
                    dbActors.put(star, actorId);
                    actorStatement.setString(1, actorId);
                    actorStatement.setString(2, star);
                    actorStatement.setString(3, null);
                    actorStatement.addBatch();
                }

                Movie tmp = new Movie(movie, director, null);
                if (dbMovies.containsKey(tmp)) {
                    String starId = dbActors.get(star);
                    String movieId = dbMovies.get(tmp);

                    if (dbSiM.containsKey(movieId)) {
                        HashSet<String> tmpMap = dbSiM.get(movieId);
                        if (!tmpMap.contains(starId)) {
                            actorMovieStatement.setString(1, starId);
                            actorMovieStatement.setString(2, movieId);
                            actorMovieStatement.addBatch();
                        }
                    } else {
                        actorMovieStatement.setString(1, starId);
                        actorMovieStatement.setString(2, movieId);
                        actorMovieStatement.addBatch();
                    }
                } else {
                    System.out.printf("Error: movies(name: %s, director: %s) does not exist in DB. \n", movie, director);
                }
            }

            actorStatement.executeBatch();
            actorMovieStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
            actorStatement.close();
            actorMovieStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("m")) {
            add = true;
            tempStarMovie = new StarMovie();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("m")) {
            //add it to the list
            if (add) {
                tempStarMovie.setDirector(tempDirector);
                parsedStarMovie.add(tempStarMovie);
            }
        } else if (qName.equalsIgnoreCase("t")) {
            if (tempVal.strip().isEmpty() || tempVal == null){
                System.out.println("Inconsistency: 't' field has en empty value.");
                add = false;
            }
            else {
                tempStarMovie.setMovie(tempVal);
            }
        } else if (qName.equals("is")) {
            if (tempVal.strip().isEmpty() || tempVal == null){
                System.out.println("Inconsistency: 'is' field has en empty value.");
                add = false;
            }
            else {
                tempDirector = tempVal;
            }
        } else if (qName.equals("a")) {
            if (tempVal.strip().isEmpty() || tempVal == null){
                System.out.println("Inconsistency: 'a' field has en empty value.");
                add = false;
            }
            else {
                tempStarMovie.setStar(tempVal);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        castParser spe = new castParser();
        spe.run();
    }
}