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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "OrderServlet", urlPatterns = "/api/order")
public class OrderServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        String orderId = request.getParameter("orderId");
        System.out.println(orderId);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            JsonArray sales = new JsonArray();

            String query = "SELECT S.id, M.title, S.saleDate, S.quantity, S.orderId\n" +
                    "FROM sales S, movies M\n" +
                    "WHERE S.orderId = ? AND S.movieId = M.id";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, orderId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                JsonObject tmp = new JsonObject();
                String saleId = rs.getString("id");
                String title = rs.getString("title");
                String saleDate = rs.getString("saleDate");
                String qty = rs.getString("quantity");

                tmp.addProperty("saleId", saleId);
                tmp.addProperty("title", title);
                tmp.addProperty("saleDate", saleDate);
                tmp.addProperty("qty", qty);

                sales.add(tmp);
            }

            statement.close();
            rs.close();
            // write JSON string to output
            out.write(sales.toString());
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