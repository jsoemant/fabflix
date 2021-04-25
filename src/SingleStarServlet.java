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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
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

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            //Preparing and executing star Query
            String starQuery = "SELECT S.id, S.name, IFNULL(S.birthYear, 'N/A') as birthYear\n" +
                    "FROM stars S\n" +
                    "WHERE S.id = ?";
            PreparedStatement starStatement = conn.prepareStatement(starQuery);
            starStatement.setString(1, id);
            ResultSet starResults = starStatement.executeQuery();

            //Prepare and execute movieQuery
            String movieQuery = "SELECT M.id, M.title\n" +
                    "FROM stars_in_movies SiM, movies M\n" +
                    "WHERE SiM.starId = ? AND SiM.movieId = M.id\n" +
                    "ORDER BY M.year DESC, M.title ASC";

            PreparedStatement movieStatement = conn.prepareStatement(movieQuery);
            movieStatement.setString(1, id);
            ResultSet movieResults = movieStatement.executeQuery();

            //Creating array for return
            JsonArray jsonArray = new JsonArray();

            // Parse and handle star Data
            JsonObject jsonObject = new JsonObject();
            while (starResults.next()) {
                String name = starResults.getString("name");
                String starId = starResults.getString("id");
                String birthYear = starResults.getString("birthYear");
                jsonObject.addProperty("name", name);
                jsonObject.addProperty("starId", starId);
                jsonObject.addProperty("birthYear", birthYear);
                jsonArray.add(jsonObject);
            }
            starResults.close();
            starStatement.close();

            // Parse and handle movie data
            JsonArray movieArray = new JsonArray();
            while (movieResults.next()) {
                JsonObject tmp = new JsonObject();
                String movieId = movieResults.getString("id");
                String movieTitle = movieResults.getString("title");
                tmp.addProperty("id", movieId);
                tmp.addProperty("title", movieTitle);
                movieArray.add(tmp);
            }
            jsonObject.add("movies", movieArray);
            movieResults.close();
            movieStatement.close();

            // write JSON string to output
            out.write(jsonArray.toString());

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