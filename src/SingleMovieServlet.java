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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        String id = request.getParameter("id");
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            // Preparing and Executing Genre Query
            String genreQuery = "SELECT G.id, G.name\n" +
                    "FROM movies M, genres G, genres_in_movies GiM\n" +
                    "WHERE M.id = ? AND GiM.movieId = ? AND GiM.genreId = G.id\n" +
                    "ORDER BY G.name ASC";
            PreparedStatement statement1 = conn.prepareStatement(genreQuery);
            statement1.setString(1, id);
            statement1.setString(2, id);
            ResultSet rs1 = statement1.executeQuery();

            // Preparing and Executing Movie Query
            String movieQuery = "SELECT M.title, M.year, M.director, " +
                    "IFNULL(R.rating, 'N/A') as rating\n" +
                    "FROM movies M LEFT OUTER JOIN ratings R ON M.id = R.movieId\n" +
                    "WHERE M.id = ?";
            PreparedStatement statement3 = conn.prepareStatement(movieQuery);
            statement3.setString(1, id);
            ResultSet rs3 = statement3.executeQuery();

            // Creating jsonArray to return
            JsonArray jsonArray = new JsonArray();

            // Create jsonObject for return
            JsonObject jsonObject = new JsonObject();

            // Parsing and handling genre Data
            JsonArray genresArray = new JsonArray();
            while (rs1.next()) {
                JsonObject tmp = new JsonObject();
                String genreId = rs1.getString("id");
                String genreName = rs1.getString("name");
                tmp.addProperty("id", genreId);
                tmp.addProperty("name", genreName);
                genresArray.add(tmp);
            }
            jsonObject.add("genres", genresArray);
            rs1.close();
            statement1.close();


            // Parsing and handling Movie Data
            while (rs3.next()) {
                String title = rs3.getString("title");
                String year = rs3.getString("year");
                String director = rs3.getString("director");
                String rating = rs3.getString("rating");
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("rating", rating);
                jsonObject.addProperty("id", id);
            }
            rs3.close();
            statement3.close();

            // Order by star
            String starQuery = "SELECT S.id, S.name,\n" +
                    "   (SELECT COUNT(*) as numPlayed\n" +
                    "       FROM stars_in_movies SiM\n" +
                    "       WHERE SiM.starId = S.id\n" +
                    "    ) as numMovies\n" +
                    "FROM movies M, stars S, stars_in_movies SiM\n" +
                    "WHERE M.id = ? AND SiM.movieId = ? AND SiM.starId = S.id\n" +
                    "ORDER BY numMovies DESC, S.name ASC\n";
            PreparedStatement starStatement = conn.prepareStatement(starQuery);
            starStatement.setString(1, id);
            starStatement.setString(2, id);
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

            // Modify and Return jsonArray
            jsonArray.add(jsonObject);
            out.write(jsonArray.toString());
            response.setStatus(200);
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}