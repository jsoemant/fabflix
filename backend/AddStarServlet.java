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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "AddStarServlet", urlPatterns = "/_addstar")
public class AddStarServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb-master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String role = user.getRole();
        if (!role.equals("employee")) {
            System.out.println("Not employee");
            response.setStatus(401);
            return;
        }
        PrintWriter out = response.getWriter();
        String name = request.getParameter("name");
        String birthYear = request.getParameter("birthYear");
        JsonObject jsonObject = new JsonObject();
        try (Connection conn = dataSource.getConnection()) {
            Statement statement = conn.createStatement();
            String query = "SELECT *\n" +
                    "FROM stars\n" +
                    "ORDER BY id DESC LIMIT 1";
            ResultSet rs = statement.executeQuery(query);
            String newId = null;
            while (rs.next()) {
                int lastId = Integer.parseInt(rs.getString("id").substring(2)) + 1;
                newId = "nm" + lastId;
            }
            rs.close();
            statement.close();
            String insertQuery= "INSERT INTO stars\n" +
                    "VALUES (?, ?, ?)";
            PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
            insertStatement.setString(1, newId);
            insertStatement.setString(2, name);
            if (birthYear.isEmpty()) {
                insertStatement.setString(3, null);
            } else {
                insertStatement.setString(3, birthYear);
            }
            insertStatement.executeUpdate();
            insertStatement.close();


            jsonObject.addProperty("id", newId);
            out.write(jsonObject.toString());
            response.setStatus(200);
        } catch (Exception e) {
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}