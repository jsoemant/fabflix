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

@WebServlet(name = "AddMovieServlet", urlPatterns = "/_addmovie")
public class AddMovieServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
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

        JsonObject jsonObject = new JsonObject();
        try (Connection conn = dataSource.getConnection()) {
            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star");
            String birthYear = request.getParameter("birthYear");
            String genre = request.getParameter("genre");

            if (birthYear.isEmpty()) {
                birthYear = null;
            }

            String query = "CALL add_movie(?, ?, ?, ?, ?, ?, @out_value)";

            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, title);
            statement.setString(2, year);
            statement.setString(3, director);
            statement.setString(4, star);
            statement.setString(5, birthYear);
            statement.setString(6, genre);

            String query2 = "SELECT @out_value";
            Statement statement2 = conn.createStatement();

            statement.executeQuery();

            ResultSet rs = statement.executeQuery(query2);

            while (rs.next()) {
                String msg = rs.getString("@out_value");
                jsonObject.addProperty("message", msg);
            }

            statement.close();
            rs.close();
            statement2.close();

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