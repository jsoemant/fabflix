import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {

    /**
     * handles GET requests to show user information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set the response type to JSON.
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        JsonObject responseJsonObject = new JsonObject();

        responseJsonObject.addProperty("role", user.getRole());

        if (user.getRole().equals("employee")) {
            out.write(responseJsonObject.toString());
            response.setStatus(200);
            return;
        }

        String id = user.getId();

        HashMap<String, HashMap<String, String>> cart = user.getCart();
        JsonArray cartArray = new JsonArray();

        for (Map.Entry item : cart.entrySet()) {
            JsonObject tmp = new JsonObject();
            String movieId = (String) item.getKey();
            HashMap<String, String> value = (HashMap<String, String>) item.getValue();

            for (Map.Entry<String, String> info : value.entrySet()) {
                String infoKey = info.getKey();
                String infoValue = info.getValue();
                tmp.addProperty(infoKey, infoValue);
            }

            tmp.addProperty("id", movieId);
            cartArray.add(tmp);
        }

        responseJsonObject.addProperty("id", id);
        responseJsonObject.add("shoppingCart", cartArray);
        // write all the data into the jsonObject
        out.write(responseJsonObject.toString());
        response.setStatus(200);
    }

    /**
     * handles POST requests to edit user information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set the response type to JSON.
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        JsonObject jsonObject = new JsonObject();
        // Movie id, title, action
        String id = request.getParameter("id");
        String title = request.getParameter("title");
        String action = request.getParameter("action");
        // actions include "add", "subtract", "remove", "delete"

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user.getRole().equals("employee")) {
            jsonObject.addProperty("status", "fail");
            out.write(jsonObject.toString());
            response.setStatus(200);
            return;
        }

        user.editCart(id, title, action);
        jsonObject.addProperty("status", "success");
        out.write(jsonObject.toString());
        response.setStatus(200);

    }
}
