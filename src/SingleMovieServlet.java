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

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

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
            String query = "WITH MR AS (SELECT *\n" +
                    "\t\t\tFROM movies M LEFT OUTER JOIN ratings R on M.id = R.movieId)\n" +
                    "\n" +
                    "SELECT \n" +
                    "\tMR.id,\n" +
                    "    MR.title,\n" +
                    "    MR.year,\n" +
                    "    MR.director,\n" +
                    "    (SELECT \n" +
                    "            GROUP_CONCAT(sub_query.name)\n" +
                    "        FROM\n" +
                    "            (SELECT \n" +
                    "                G.name\n" +
                    "            FROM\n" +
                    "                genres G, genres_in_movies GM, movies TM\n" +
                    "            WHERE\n" +
                    "                TM.id = MR.id AND MR.id = GM.movieId\n" +
                    "                    AND GM.genreId = G.id) AS sub_query) AS genres,\n" +
                    "    (SELECT \n" +
                    "            GROUP_CONCAT(sub_query.name)\n" +
                    "        FROM\n" +
                    "            (SELECT \n" +
                    "                S.name\n" +
                    "            FROM\n" +
                    "                stars S, stars_in_movies SM, movies TM\n" +
                    "            WHERE\n" +
                    "                TM.id = MR.id AND MR.id = SM.movieId\n" +
                    "                    AND SM.starId = S.id) AS sub_query) AS stars,\n" +
                    "\t(SELECT \n" +
                    "            GROUP_CONCAT(sub_query.id)\n" +
                    "        FROM\n" +
                    "            (SELECT \n" +
                    "                S.id\n" +
                    "            FROM\n" +
                    "                stars S, stars_in_movies SM, movies TM\n" +
                    "            WHERE\n" +
                    "                TM.id = MR.id AND MR.id = SM.movieId\n" +
                    "                    AND SM.starId = S.id) AS sub_query) AS stars_id,\n" +
                    "\tIFNULL(MR.rating, 'N/A') as rating\n" +
                    "FROM\n" +
                    "\tMR\n" +
                    "WHERE\n" +
                    "    MR.id = " + "?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, requestId);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while(rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                String genres = rs.getString("genres");
                JsonArray starsArray = new JsonArray();
                String[] stars = rs.getString("stars").split(",");
                for (String star: stars){
                    starsArray.add(star);
                }
                JsonArray starsIdArray = new JsonArray();
                String[] starsId = rs.getString("stars_id").split(",");
                for (String starId: starsId){
                    starsIdArray.add(starId);
                }
                String rating = rs.getString("rating");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("genres", genres);
                jsonObject.addProperty("rating", rating);
                jsonObject.add("stars", starsArray);
                jsonObject.add("stars_id", starsIdArray);

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
