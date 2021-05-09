import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "MetadataServlet", urlPatterns = "/_metadata")
public class MetadataServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String role = user.getRole();

        if (!role.equals("employee")) {
            System.out.println("Not employee");
            response.setStatus(401);
            return;
        }

        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            JsonArray responseArray = new JsonArray();

            Statement statement = conn.createStatement();

            String query = "select TABLE_NAME, COLUMN_NAME, COLUMN_TYPE\n" +
                    "from information_schema.columns\n" +
                    "where table_schema='moviedb'";

            ResultSet rs = statement.executeQuery(query);

            String prevTable = "";

            JsonObject tableObject = new JsonObject();
            JsonArray columnArray = new JsonArray();

            while (rs.next()) {

                String table = rs.getString("TABLE_NAME");
                if (prevTable.isEmpty()) {
                    prevTable = table;
                }

                JsonObject column = new JsonObject();

                String name = rs.getString("COLUMN_NAME");
                String type = rs.getString("COLUMN_TYPE");

                if (!table.equals(prevTable)) {
                    tableObject.addProperty("table_name", prevTable);
                    tableObject.add("columns", columnArray);
                    responseArray.add(tableObject);

                    tableObject = new JsonObject();
                    columnArray = new JsonArray();

                    prevTable = table;
                }

                column.addProperty("column_name", name);
                column.addProperty("column_type", type);
                columnArray.add(column);

            }

            // Final add
            tableObject.addProperty("table_name", prevTable);
            tableObject.add("columns", columnArray);
            responseArray.add(tableObject);

            rs.close();
            statement.close();
            out.write(responseArray.toString());
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