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
import java.sql.PreparedStatement;
import java.io.FileWriter;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movie-list"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private DataSource dataSource;
    private String requestQuery;
    private String countQuery;
    private int currentIndex;
    private int currentLength;
    long startTime;
    long startTime2;
    long totalTime;
    long totalTime2;


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

//        if (!isNullEmpty(title)) {
//            if (title.equals("*")) {
//                whereClause += whereSep + "REGEXP_LIKE(M.title, '^[^a-zA-Z0-9]')";
//            }
//            else {
//                whereClause += whereSep + "M.title LIKE ?";
//            }
//            whereSep = " AND ";
//        }

        if (!isNullEmpty(title)) {
            if (title.equals("*")) {
                whereClause += whereSep + "REGEXP_LIKE(M.title, '^[^a-zA-Z0-9]')";
            }
            else {
                whereClause += whereSep + "MATCH (M.title) AGAINST (? in BOOLEAN MODE)";
            }
            whereSep = " AND ";
        }

        if (!isNullEmpty(year)) {
            whereClause += whereSep + "M.year = ?";
            whereSep = " AND ";
        }

        if (!isNullEmpty(director)) {
            whereClause += whereSep + "M.director LIKE ?";
            whereSep = " AND ";
        }

        if (!isNullEmpty(star)) {
            fromClause += fromSep + "stars S";
            fromClause += fromSep + "stars_in_movies SiM";
            whereClause += whereSep + "M.id = SiM.movieId";
            whereSep = " AND ";
            whereClause += whereSep + "S.id = SiM.starId";
            whereSep = " AND ";
            whereClause += whereSep + "S.name LIKE ?";
            whereSep = " AND ";
        }

        if (!isNullEmpty(genre)) {
            fromClause += fromSep + "genres G";
            fromClause += fromSep + "genres_in_movies GiM";
            whereClause += whereSep + "M.id = GiM.movieId";
            whereSep = " AND ";
            whereClause += whereSep + "G.id = GiM.genreId";
            whereSep = " AND ";
            whereClause += whereSep + "G.id = ?";
            whereSep = " AND ";
        }

        if (!isNullEmpty(order)) {
            if (order.equals("TITLEASCRATINGASC")) {
                orderClause += "M.title ASC, R.rating ASC";
            } else if (order.equals("TITLEASCRATINGDESC")) {
                orderClause += "M.title ASC, R.rating DESC";
            } else if (order.equals("TITLEDESCRATINGASC")) {
                orderClause += "M.title DESC, R.rating ASC";
            } else if (order.equals("TITLEDESCRATINGDESC")) {
                orderClause += "M.title DESC, R.rating DESC";
            } else if (order.equals("RATINGASCTITLEASC")) {
                orderClause += "R.rating ASC, M.title ASC";
            } else if (order.equals("RATINGASCTITLEDESC")) {
                orderClause += "R.rating ASC, M.title DESC";
            } else if (order.equals("RATINGDESCTITLEASC")) {
                orderClause += "R.rating DESC, M.title ASC";
            } else if (order.equals("RATINGDESCTITLEDESC")) {
                orderClause += "R.rating DESC, M.title DESC";
            }
        }

        if (!isNullEmpty(limit)) {
            limitClause += "?";
            currentLength = Integer.parseInt(limit);
        } else {
            currentLength = -1;
        }

        if (!isNullEmpty(offset)) {
            offsetClause += "?";
            currentIndex = Integer.parseInt(offset);
        } else {
            currentIndex = -1;
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
                    (limitClause.equals("LIMIT ") ? "" : ("\n" + limitClause)) +
                    (offsetClause.equals("OFFSET ") ? "" : ("\n" + offsetClause + "\n"));
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
        startTime = System.nanoTime();
        response.setContentType("application/json");

        // Retrieve parameter id from url request.
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String genre = request.getParameter("genre");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String limit = request.getParameter("limit");
        String offset = request.getParameter("offset");

        buildQuery(request);
        PrintWriter out = response.getWriter();

        String nTitle = "";
        String[] sTitle = title.split(" ");
        String ad = "+";
        for (String i: sTitle){
            nTitle += ad + i + "*";
            ad = " +";
        }

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(requestQuery);
            int count = 1;

//            System.out.println(requestQuery);

            if (!isNullEmpty(title)) {
                if (!title.equals("*") && title.length() == 1 ) {
                    statement.setString(count++, title + '%');
                } else if (title.length() > 1) {
                    statement.setString(count++, nTitle);
                }
            }
            System.out.println(statement);
            if (!isNullEmpty(year)) {
                statement.setInt(count++, Integer.parseInt(year));
            }
            if (!isNullEmpty(director)) {
                statement.setString(count++, '%' + director + '%');
            }
            if (!isNullEmpty(star)) {
                statement.setString(count++, '%' + star + '%');
            }
            if (!isNullEmpty(genre)) {
                statement.setInt(count++, Integer.parseInt(genre));
            }
            if (!isNullEmpty(limit)) {
                statement.setInt(count++, Integer.parseInt(limit));
            }
            if (!isNullEmpty(offset)) {
                statement.setInt(count++, Integer.parseInt(offset));
            }
            System.out.println(statement);
            startTime2 = System.nanoTime();
            ResultSet rs = statement.executeQuery();
            totalTime2 = System.nanoTime() - startTime2;
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

            PreparedStatement countStatement = conn.prepareStatement(countQuery);
            count = 1;

//            if (!isNullEmpty(title)) {
//                if (!title.equals("*") && title.length() == 1 ) {
//                    countStatement.setString(count++, title + '%');
//                } else if (title.length() > 1) {
//                    countStatement.setString(count++, '%' + title + '%');
//                }
//            }
            if (!isNullEmpty(title)) {
                if (!title.equals("*") && title.length() == 1 ) {
                    countStatement.setString(count++, title + '%');
                } else if (title.length() > 1) {
                    countStatement.setString(count++, nTitle);
                }
            }

            if (!isNullEmpty(year)) {
                countStatement.setInt(count++, Integer.parseInt(year));
            }
            if (!isNullEmpty(director)) {
                countStatement.setString(count++, '%' + director + '%');
            }
            if (!isNullEmpty(star)) {
                countStatement.setString(count++, '%' + star + '%');
            }
            if (!isNullEmpty(genre)) {
                countStatement.setInt(count++, Integer.parseInt(genre));
            }

            // Get max count
            ResultSet movieCountSet = countStatement.executeQuery();
            int maxResults = -1;
            while (movieCountSet.next()) {
                maxResults = movieCountSet.getInt("maxResults");
            }
            returnObject.addProperty("max", maxResults);
            movieCountSet.close();
            countStatement.close();

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
            totalTime = System.nanoTime() - startTime;
            System.out.println("hellojsdfkljdslkfj");
            System.out.println(request.getServletContext().getRealPath("/") + "log.txt");
            try {
                FileWriter myWriter = new FileWriter("/home/ubuntu/log.txt", true);
                myWriter.write(totalTime + " | " + totalTime2 + "\n");
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            out.close();
        }
        // always remember to close db connection after usage. Here it's done by try-with-resources

    }
}