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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        String currentTime = LocalDate.now().toString();
        HttpSession session = request.getSession();

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            HashMap<String, HashMap<String, String>> shoppingCart = (HashMap<String, HashMap<String, String>>) session.getAttribute("shoppingCart");


            JsonArray sales = new JsonArray();

            String id = (String) session.getAttribute("id");

            String insertQuery= "INSERT INTO sales\n" +
                    "VALUES (NULL, ?, ?, ?)\n";

            String saleIdQuery = "SELECT S.id\n" +
                    "FROM sales S\n" +
                    "ORDER BY S.id DESC\n" +
                    "LIMIT 1";

            PreparedStatement updateStatement = conn.prepareStatement(insertQuery);
            Statement saleIdStatement = conn.createStatement();

            for (Map.Entry entry : shoppingCart.entrySet()) {
                String key = (String) entry.getKey();
                HashMap<String, String> value = (HashMap<String, String>) entry.getValue();
                String qty = value.get("qty");
                String title = value.get("title");

                for (int i=0; i<Integer.parseInt(qty); i++) {
                    JsonObject tmp = new JsonObject();

                    updateStatement.setString(1, id);
                    updateStatement.setString(2, key);
                    updateStatement.setString(3, currentTime);
                    updateStatement.executeUpdate();

                    tmp.addProperty("movieId", key);
                    tmp.addProperty("title", title);

                    ResultSet res = saleIdStatement.executeQuery(saleIdQuery);

                    while (res.next()) {
                        String saleId = res.getString("id");
                        tmp.addProperty("saleId", saleId);
                    }

                    res.close();
                    sales.add(tmp);
                }
            }


            saleIdStatement.close();
            updateStatement.close();

            // write JSON string to output
            out.write(sales.toString());
            // set response status to 200 (OK)
            response.setStatus(200);


        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "fail");
            jsonObject.addProperty("message", "credit card information is incorrect");
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