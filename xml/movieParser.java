import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class movieParser extends DefaultHandler {
    // XML
    private String tempVal;
    private Movie tempMovie;
    private String tempDirector;
    private boolean addMovie;
    // XML RESULT
    private ArrayList<Movie> parsedMovies;
    // DB CACHE
    private HashSet<Movie> dbMovies;
    private int maxMovieID;
    private HashMap<String, Integer> dbGenres;
    private int maxGenreID;
    private HashMap<String, HashSet<Integer>> dbGiM;
    // DB CONNECTION
    private Connection connection;


    public movieParser() throws Exception {
        // DB CONNECTION
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        // XML
        this.parsedMovies = new ArrayList<>();
        // DB CACHE
        initDB();
    }

    private void initDB() throws SQLException {
        dbMovies = new HashSet<Movie>();
        dbGenres = new HashMap<String, Integer>();
        dbGiM = new HashMap<String, HashSet<Integer>>();
        Statement statement = connection.createStatement();
        // MOVIE
        String movieQuery = "SELECT * FROM movies";
        ResultSet movieSet = statement.executeQuery(movieQuery);
        while (movieSet.next()) {
            String movieName = movieSet.getString("title");
            String movieDirector = movieSet.getString("director");
            String movieYear = movieSet.getString("year");
            dbMovies.add(new Movie(movieName, movieDirector, movieYear));
        }
        movieSet.close();
        // MOVIE ID
        String movieIDQuery = "SELECT MAX(id) FROM movies";
        ResultSet movieIDSet = statement.executeQuery(movieIDQuery);
        while (movieIDSet.next()) {
            String movieID = movieIDSet.getString("MAX(id)");
            maxMovieID = Integer.parseInt(movieID.substring(2));
        }
        movieIDSet.close();
        // GENRE
        String genreQuery = "SELECT * FROM genres";
        ResultSet genreSet = statement.executeQuery(genreQuery);
        while (genreSet.next()) {
            Integer id = genreSet.getInt("id");
            String name = genreSet.getString("name").toLowerCase().trim();
            dbGenres.put(name, id);
        }
        genreSet.close();
        // GENRE ID
        String genreIDQuery = "SELECT MAX(id) FROM genres";
        ResultSet genreIDSet = statement.executeQuery(genreIDQuery);
        while (genreIDSet.next()) {
            String id = genreIDSet.getString("MAX(id)");
            maxGenreID = Integer.parseInt(id);
        }
        genreIDSet.close();
        // GENRE in MOVIES
        String gimQuery = "SELECT * FROM genres_in_movies";
        ResultSet gimSet = statement.executeQuery(gimQuery);
        while (gimSet.next()) {
            int genreId = gimSet.getInt("genreId");
            String movieId = gimSet.getString("movieId");

            HashSet<Integer> tmp;
            if (dbGiM.containsKey(movieId)) {
                tmp = dbGiM.get(movieId);
            } else {
                tmp = new HashSet<Integer>();
            }

            tmp.add(genreId);
            dbGiM.put(movieId, tmp);
        }
        gimSet.close();

        statement.close();
    }

    public void run() throws SQLException {
        String movieDocument = "mains243.xml";
        parseDocument(movieDocument);
        insertMovies();
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

    private void insertMovies() {
        PreparedStatement movieStatement = null;
        String movieInsert = "INSERT INTO movies VALUES(?, ?, ?, ?)";

        PreparedStatement genreStatement = null;
        String genreInsert = "INSERT INTO genres VALUES(?, ?)";

        PreparedStatement gimStatement = null;
        String gimInsert = "INSERT INTO genres_in_movies VALUES(?, ?)";

        try {
            connection.setAutoCommit(false);
            movieStatement = connection.prepareStatement(movieInsert);
            genreStatement = connection.prepareStatement(genreInsert);
            gimStatement = connection.prepareStatement(gimInsert);

            for (Movie movie: parsedMovies) {
                String title = movie.getTitle();
                String director = movie.getDirector();
                String year = movie.getYear();

                if (dbMovies.contains(movie)) {
                    System.out.printf("Error: movies(title: %s, director: %s, year: %s) exists in DB.\n", title, director, year);
                } else {
                    // MOVIE
                    dbMovies.add(movie);
                    maxMovieID++;
                    String movieId = "tt" + maxMovieID;
                    movieStatement.setString(1, movieId);
                    movieStatement.setString(2, title);
                    movieStatement.setString(3, year);
                    movieStatement.setString(4, director);
                    movieStatement.addBatch();
                    // GENRE
                    for (String genre: movie.getGenres()) {
                        String x = genre.toLowerCase().trim();

                        if (!dbGenres.containsKey(x)) {
                            maxGenreID++;
                            dbGenres.put(x, maxGenreID);
                            genreStatement.setInt(1, maxGenreID);
                            genreStatement.setString(2, genre);
                            genreStatement.addBatch();
                        }

                        if (dbGiM.containsKey(movieId) && (dbGiM.get(movieId).contains(dbGenres.get(x)))) {
                            System.out.printf("Error: genres_in_movies(genreId: %s, movieId: %s) exists in DB.\n", genre, movieId);
                        } else {
                            gimStatement.setInt(1, dbGenres.get(x));
                            gimStatement.setString(2, movieId);
                            gimStatement.addBatch();
                        }
                    }
                }
            }
            movieStatement.executeBatch();
            genreStatement.executeBatch();
            gimStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
            movieStatement.close();
            genreStatement.close();
            gimStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            addMovie = true;
            tempMovie = new Movie();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("film")) {
            if (addMovie) {
                tempMovie.setDirector(tempDirector);
                parsedMovies.add(tempMovie);
            }
        } else if (qName.equalsIgnoreCase("t")) {
            if (tempVal.trim().isEmpty()) {
                System.out.printf("Inconsistency: %s has 't' field with empty value.\n", tempMovie.getTitle());
                addMovie = false;
            } else {
                tempMovie.setTitle(tempVal);
            }
        } else if (qName.equalsIgnoreCase("year")) {
            try {
                Integer.parseInt(tempVal);
                tempMovie.setYear(tempVal);
            } catch (Exception e) {
                addMovie = false;
                System.out.printf("Inconsistency: %s has 'year' field with non-integer value.\n", tempMovie.getTitle());
            }
        } else if (qName.equalsIgnoreCase("cat")) {
            if (tempVal.trim().isEmpty()) {
                System.out.printf("Inconsistency: %s has 'cat' field with empty value.\n", tempMovie.getTitle());
            } else {
                tempMovie.addGenre(tempVal.trim());
            }
        } else if (qName.equalsIgnoreCase("dirname")) {
            tempDirector = tempVal;
        }
    }

    public static void main(String[] args) throws Exception {
        movieParser spe = new movieParser();
        spe.run();
    }
}