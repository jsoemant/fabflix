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
import java.util.HashMap;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movie-list"
@WebServlet(name = "AutoCompleteServlet", urlPatterns = "/api/autocomplete")
public class AutoCompleteServlet extends HttpServlet {
    private DataSource dataSource;
//    public static HashMap<Integer, String> movieMap = new HashMap<>();

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

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        // Retrieve parameter id from url request.
        String title = request.getParameter("query");

        String query = "SELECT M.id, M.title, M.year, M.director, R.rating\n" +
                "FROM movies M LEFT OUTER JOIN ratings R ON M.id = R.movieId\n" +
                "WHERE MATCH (M.title) AGAINST (? in BOOLEAN MODE)\n" +
                "ORDER BY M.title ASC, R.rating ASC\n" +
                "LIMIT 10\n" +
                "OFFSET 0";

        PrintWriter out = response.getWriter();

        String nTitle = "";
        String[] sTitle = title.split(" ");
        String ad = "+";
        for (String i: sTitle){
            nTitle += ad + i + "*";
            ad = " +";
        }

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            int count = 1;
            if (!isNullEmpty(title)) {
                if (!title.equals("*") && title.length() == 1 ) {
                    statement.setString(count++, title + '%');
                } else if (title.length() > 1) {
                    statement.setString(count++, nTitle);
                }
            }
            System.out.println(statement);

            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            // Iterate through each row of rs
            while (rs.next()) {
                // Parse movie data
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");

                jsonArray.add(generateJsonObject(movie_id, movie_title));
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

    private static JsonObject generateJsonObject(String id, String name) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", name);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("id", id);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }
}