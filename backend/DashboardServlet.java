
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet(name = "DashboardServlet", urlPatterns = "/_dashboard")
public class DashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");

        String role = user.getRole();

        if (role.equals("employee")) {
            System.out.println("employee");

            RequestDispatcher view = request.getRequestDispatcher("/dashboard.html");
            view.forward(request, response);
        }
        else {
            System.out.println("Not employee");
            response.setStatus(401);
        }

    }
}