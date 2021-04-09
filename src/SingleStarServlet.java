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
    private static final long serialVersionUID = 2L;

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
        String requestId = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT \n" +
                    "    S.id,\n" +
                    "    S.name,\n" +
                    "    IFNULL(S.birthYear, 'N/A') as birthYear,\n" +
                    "    (SELECT \n" +
                    "            GROUP_CONCAT(sub_query.id)\n" +
                    "        FROM\n" +
                    "            (SELECT \n" +
                    "                M.id\n" +
                    "            FROM\n" +
                    "                stars_in_movies SM, movies M\n" +
                    "            WHERE\n" +
                    "                S.id = SM.starId AND SM.movieId = M.id) AS sub_query) AS movies_id,\n" +
                    "    (SELECT \n" +
                    "            GROUP_CONCAT(sub_query.title)\n" +
                    "        FROM\n" +
                    "            (SELECT \n" +
                    "                M.title\n" +
                    "            FROM\n" +
                    "                stars_in_movies SM, movies M\n" +
                    "            WHERE\n" +
                    "                S.id = SM.starId AND SM.movieId = M.id) AS sub_query) AS movies\n" +
                    "FROM\n" +
                    "    stars S\n" +
                    "WHERE\n" +
                    "    S.id = " + "?" ;

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, requestId);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String id = rs.getString("id");
                String name = rs.getString("name");
                String birthYear = rs.getString("birthYear");

                JsonArray moviesArray = new JsonArray();
                String[] movies = rs.getString("movies").split(",");
                for (String movie: movies) {
                    moviesArray.add(movie);
                }

                JsonArray moviesIdArray = new JsonArray();
                String[] moviesId = rs.getString("movies_id").split(",");
                for (String Id: moviesId) {
                    moviesIdArray.add(Id);
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("name", name);
                jsonObject.addProperty("birthYear", birthYear);
                jsonObject.add("movies", moviesArray);
                jsonObject.add("moviesId", moviesIdArray);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // write JSON string to output
            out.write(jsonArray.toString());
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
