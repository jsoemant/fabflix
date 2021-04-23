import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movie-list"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {

    // Create a dataSource which registered in web.
    private DataSource dataSource;
    private String requestQuery;
    private String countQuery;
    private int currentIndex;
    private int currentLength;


    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public boolean isNullEmpty(String s) {
        return (s == null || s.isEmpty());
    }

    public void buildQuery(HttpServletRequest request) {
        // Build Query Parts
        String selectClause = "SELECT M.id, M.title, M.year, M.director, R.rating";
        String fromClause = "FROM movies M LEFT OUTER JOIN ratings R ON M.id = R.movieId";
        String whereClause = "WHERE ";
        String orderClause = "ORDER BY ";
        String limitClause = "LIMIT ";
        String offsetClause = "OFFSET ";

        // Retrieve parameter id from url request.
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String genre = request.getParameter("genre");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String order = request.getParameter("order");
        String limit = request.getParameter("limit");
        String offset = request.getParameter("offset");

        // Check each parameter
        String fromSep = ", ";
        String whereSep = "";

        if (!isNullEmpty(title)) {
            if (title.equals("*")) {
                whereClause += whereSep + "REGEXP_LIKE(M.title, '^[^a-zA-Z0-9]')";
            } else {
                whereClause += whereSep + "M.title LIKE '" + title + "%'";
            }
            whereSep = " AND ";
        }

        if (!isNullEmpty(year)) {
            whereClause += whereSep + "M.year = " + year;
            whereSep = " AND ";
        }

        if (!isNullEmpty(director)) {
            whereClause += whereSep + "M.director LIKE '%" + director + "%'";
            whereSep = " AND ";
        }

        if (!isNullEmpty(star)) {
            fromClause += fromSep + "stars S";
            fromClause += fromSep + "stars_in_movies SiM";
            whereClause += whereSep + "M.id = SiM.movieId";
            whereSep = " AND ";
            whereClause += whereSep + "S.id = SiM.starId";
            whereSep = " AND ";
            whereClause += whereSep + "S.name LIKE '%" + star + "%'";
            whereSep = " AND ";
        }

        if (!isNullEmpty(genre)) {
            fromClause += fromSep + "genres G";
            fromClause += fromSep + "genres_in_movies GiM";
            whereClause += whereSep + "M.id = GiM.movieId";
            whereSep = " AND ";
            whereClause += whereSep + "G.id = GiM.genreId";
            whereSep = " AND ";
            whereClause += whereSep + "G.id = " + genre;
            whereSep = " AND ";
        }

        if (!isNullEmpty(order)) {
            if (order.equals("TITLEASCRATINGASC")) {
                orderClause += "TITLE ASC, RATING ASC";
            } else if (order.equals("TITLEASCRATINGDESC")) {
                orderClause += "TITLE ASC, RATING DESC";
            } else if (order.equals("TITLEDESCRATINGASC")) {
                orderClause += "TITLE DESC, RATING ASC";
            } else if (order.equals("TITLEDESCRATINGDESC")) {
                orderClause += "TITLE DESC, RATING DESC";
            } else if (order.equals("RATINGASCTITLEASC")) {
                orderClause += "RATING ASC, TITLE ASC";
            } else if (order.equals("RATINGASCTITLEDESC")) {
                orderClause += "RATING ASC, TITLE DESC";
            } else if (order.equals("RATINGDESCTITLEASC")) {
                orderClause += "RATING DESC, TITLE ASC";
            } else if (order.equals("RATINGDESCTITLEDESC")) {
                orderClause += "RATING DESC, TITLE DESC";
            }
        }

        if (!isNullEmpty(limit)) {
            limitClause += limit;
            currentLength = Integer.parseInt(limit);
        } else {
            limitClause += "10";
            currentLength = 10;
        }

        if (!isNullEmpty(offset)) {
            offsetClause += offset;
            currentIndex = Integer.parseInt(offset);
        } else {
            offsetClause += "0";
            currentIndex = 0;
        }

        // Build Query
        if (isNullEmpty(whereSep)) {
            requestQuery = "";
            countQuery = "";
        } else {
            requestQuery = selectClause + "\n" +
                    fromClause + "\n" +
                    whereClause +
                    (orderClause.equals("ORDER BY ") ? "" : ("\n" + orderClause)) +
                    "\n" + limitClause +
                    "\n" + offsetClause;
            countQuery = "SELECT COUNT(*) as maxResults\n" +
                    fromClause + "\n" +
                    whereClause +
                    (orderClause.equals("ORDER BY ") ? "" : ("\n" + orderClause));
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        buildQuery(request);
        
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            // Perform the query
            ResultSet rs = statement.executeQuery(requestQuery);

            JsonObject returnObject = new JsonObject();
            JsonArray dataArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                // Parse movie data
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();

                // Add movie data
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);

                // Order by genre
                String genreQuery = "SELECT G.id, G.name\n" +
                        "FROM genres G, genres_in_movies GiM\n" +
                        "WHERE GiM.movieId = ? AND GiM.genreId = G.id\n" +
                        "ORDER BY G.name ASC\n" +
                        "LIMIT 3";
                PreparedStatement genreStatement = conn.prepareStatement(genreQuery);
                genreStatement.setString(1, movie_id);
                ResultSet genreSet = genreStatement.executeQuery();

                // Parse genre data
                JsonArray genresArray = new JsonArray();
                while (genreSet.next()) {
                    JsonObject tmp = new JsonObject();
                    String genreId = genreSet.getString("id");
                    String genreName = genreSet.getString("name");
                    tmp.addProperty("id", genreId);
                    tmp.addProperty("name", genreName);
                    genresArray.add(tmp);
                }

                // Add genre data
                jsonObject.add("genres", genresArray);
                genreSet.close();
                genreStatement.close();

                // Order by star
                String starQuery = "SELECT S.id, S.name,\n" +
                        "   (SELECT COUNT(*) as numPlayed\n" +
                        "       FROM stars_in_movies SiM\n" +
                        "       WHERE SiM.starId = S.id\n" +
                        "    ) as numMovies\n" +
                        "FROM movies M, stars S, stars_in_movies SiM\n" +
                        "WHERE M.id = ? AND SiM.movieId = ? AND SiM.starId = S.id\n" +
                        "ORDER BY numMovies DESC, S.name ASC\n" +
                        "LIMIT 3";
                PreparedStatement starStatement = conn.prepareStatement(starQuery);
                starStatement.setString(1, movie_id);
                starStatement.setString(2, movie_id);
                ResultSet starSet = starStatement.executeQuery();

                // Parse star data
                JsonArray starsArray = new JsonArray();
                while (starSet.next()) {
                    JsonObject tmp = new JsonObject();
                    String starId = starSet.getString("id");
                    String starName = starSet.getString("name");
                    tmp.addProperty("id", starId);
                    tmp.addProperty("name", starName);
                    starsArray.add(tmp);
                }

                // Add star data
                jsonObject.add("stars", starsArray);
                starSet.close();
                starStatement.close();


                // Add movie Object
                dataArray.add(jsonObject);
            }

            // Get max count
            ResultSet movieCountSet = statement.executeQuery(countQuery);
            while (movieCountSet.next()) {
                int maxResults = movieCountSet.getInt("maxResults");

                returnObject.addProperty("max", maxResults);
            }

            returnObject.addProperty("index", currentIndex);
            returnObject.addProperty("limit", currentLength);
            returnObject.add("data", dataArray);

            rs.close();
            statement.close();

            // write JSON string to output
            out.write(returnObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);


        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
        // always remember to close db connection after usage. Here it's done by try-with-resources

    }
}