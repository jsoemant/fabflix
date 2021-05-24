import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

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

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        JsonObject jsonObject = new JsonObject();

        boolean captchaExists = true;

        if (gRecaptchaResponse == null){
            captchaExists = false;
        }
        // Verify reCAPTCHA
        if (captchaExists) {
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                jsonObject.addProperty("status", "fail");
                jsonObject.addProperty("message", "captcha is not completed");
                out.write(jsonObject.toString());
                response.setStatus(200);
                out.close();
                return;
            }
        }

        try (Connection conn = dataSource.getConnection()) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String role = request.getParameter("role");
            role = "customer";

            System.out.println(password);

            String query;

            if (role.equals("customer")) {
                query = "SELECT *\n" +
                        "FROM customers C\n" +
                        "WHERE C.email = ?";
            } else {
                query = "SELECT *\n" +
                        "FROM employees E\n" +
                        "WHERE E.email = ?";
            }

            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, username);

            ResultSet rs = statement.executeQuery();

            if (rs.isBeforeFirst()) {

                while (rs.next()) {
                    String encryptedPassword = rs.getString("password");

                    if (new StrongPasswordEncryptor().checkPassword(password, encryptedPassword)) {
                        if (role.equals("customer")) {
                            String id = rs.getString("id");
                            request.getSession().setAttribute("user", new User(username, id));
                            jsonObject.addProperty("type", "customer");
                        } else {
                            request.getSession().setAttribute("user", new User(username));
                            jsonObject.addProperty("type", "employee");
                        }
                        System.out.println("success here");
                        jsonObject.addProperty("status", "success");
                        jsonObject.addProperty("message", "login information is correct");
                    } else {
                        System.out.println("failing here");
                        jsonObject.addProperty("status", "fail");
                        jsonObject.addProperty("message", "login information is incorrect");
                    }
                }
            } else {
                jsonObject.addProperty("status", "fail");
                jsonObject.addProperty("message", "username does not exist");
            }

            rs.close();
            statement.close();
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
