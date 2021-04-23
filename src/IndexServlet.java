import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type


        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();
        String id = (String) session.getAttribute("id");

        JsonArray jsonArray = new JsonArray();
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("id", id);
        responseJsonObject.addProperty("sessionID", sessionId);

        HashMap<String, String> searchParameters = (HashMap<String, String>) session.getAttribute("searchParameters");
        HashMap<String, HashMap<String, String>> shoppingCart = (HashMap<String, HashMap<String, String>>) session.getAttribute("shoppingCart");

        if (searchParameters == null) {
            searchParameters = new HashMap<String, String>();
            searchParameters.put("title", "");
            searchParameters.put("year", "");
            searchParameters.put("director", "");
            searchParameters.put("genre", "");
            searchParameters.put("star", "");
            searchParameters.put("limit", "10");
            searchParameters.put("order", "TITLEASCRATINGASC");
            searchParameters.put("offset", "0");
            session.setAttribute("searchParameters", searchParameters);

            shoppingCart = new HashMap<String, HashMap<String, String>>();
            session.setAttribute("shoppingCart", shoppingCart);
        }

        JsonArray searchParametersArray = new JsonArray();
        for (Map.Entry<String, String> entry : searchParameters.entrySet()) {
            JsonObject tmp = new JsonObject();
            String key = entry.getKey();
            String value = entry.getValue();
            tmp.addProperty(key, value);
            searchParametersArray.add(tmp);
        }

        JsonArray shoppingCartArray = new JsonArray();
        for (Map.Entry entry : shoppingCart.entrySet()) {
            JsonObject tmp = new JsonObject();

            String key = (String) entry.getKey();
            HashMap<String, String> value = (HashMap<String, String>) entry.getValue();

            JsonObject tmp2 = new JsonObject();

            for (Map.Entry<String, String> entry2 : value.entrySet()) {
                String key2 = entry2.getKey();
                String value2 = entry2.getValue();
                tmp2.addProperty(key2, value2);
            }

            tmp.add(key, tmp2);
            shoppingCartArray.add(tmp);
        }

        responseJsonObject.add("searchParameters", searchParametersArray);
        responseJsonObject.add("shoppingCart", shoppingCartArray);


        jsonArray.add(responseJsonObject);

        // write all the data into the jsonObject
        response.getWriter().write(jsonArray.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        // Search parameters stuff
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String genre = request.getParameter("genre");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String limit = request.getParameter("limit");
        String order = request.getParameter("order");
        String offset = request.getParameter("offset");

        Map<String, String> searchParameters = (HashMap<String, String>) session.getAttribute("searchParameters");

        synchronized (searchParameters) {
            if (title != null) {
                searchParameters.put("title", title);
            }
            if (year != null) {
                searchParameters.put("year", year);
            }
            if (genre != null) {
                searchParameters.put("genre", genre);
            }
            if (director != null) {
                searchParameters.put("director", director);
            }
            if (star != null) {
                searchParameters.put("star", star);
            }
            if (limit != null) {
                searchParameters.put("limit", limit);
            }
            if (order != null) {
                searchParameters.put("order", order);
            }
            if (offset != null) {
                searchParameters.put("offset", offset);
            }
        }

        // Cart stuff

        String addId = request.getParameter("addId");
        String addTitle = request.getParameter("addTitle");
        String addQty = request.getParameter("addQty");
        String addPrice = request.getParameter("addPrice");

        HashMap<String, HashMap<String, String>> shoppingCart = (HashMap<String, HashMap<String, String>>) session.getAttribute("shoppingCart");

        synchronized (shoppingCart) {
            if (addId != null) {
                if (shoppingCart.containsKey(addId)) {
                    HashMap<String, String> item = shoppingCart.get(addId);
                    Integer qty = Integer.parseInt(item.get("qty"));
                    Integer qty2 = Integer.parseInt(addQty);
                    Integer total = qty + qty2;
                    if (total == 0) {
                        shoppingCart.remove(addId);
                    } else {
                        item.put("qty", Integer.toString(total));
                        shoppingCart.put(addId, item);
                    }
                } else {
                    HashMap<String, String> tmp = new HashMap<String, String>();
                    tmp.put("title", addTitle);
                    tmp.put("qty", addQty);
                    tmp.put("price", addPrice);
                    shoppingCart.put(addId, tmp);
                }
            }
        }

        // Return stuff
        JsonArray jsonArray = new JsonArray();
        JsonObject responseJsonObject = new JsonObject();

        JsonArray searchParametersArray = new JsonArray();
        for (Map.Entry<String, String> entry : searchParameters.entrySet()) {
            JsonObject tmp = new JsonObject();
            String key = entry.getKey();
            String value = entry.getValue();
            tmp.addProperty(key, value);
            searchParametersArray.add(tmp);
        }

        JsonArray shoppingCartArray = new JsonArray();
        for (Map.Entry entry : shoppingCart.entrySet()) {
            JsonObject tmp = new JsonObject();

            String key = (String) entry.getKey();
            HashMap<String, String> value = (HashMap<String, String>) entry.getValue();

            JsonObject tmp2 = new JsonObject();

            for (Map.Entry<String, String> entry2 : value.entrySet()) {
                String key2 = entry2.getKey();
                String value2 = entry2.getValue();
                tmp2.addProperty(key2, value2);
            }

            tmp.add(key, tmp2);
            shoppingCartArray.add(tmp);
        }

        responseJsonObject.add("searchParameters", searchParametersArray);
        responseJsonObject.add("shoppingCart", shoppingCartArray);

        jsonArray.add(responseJsonObject);

        // write all the data into the jsonObject
        response.getWriter().write(jsonArray.toString());
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String id = request.getParameter("addId");

        HashMap<String, HashMap<String, String>> shoppingCart = (HashMap<String, HashMap<String, String>>) session.getAttribute("shoppingCart");
        JsonObject responseObject = new JsonObject();


        synchronized (shoppingCart) {
            if (id != null) {
                if (shoppingCart.containsKey(id)) {
                    shoppingCart.remove(id);
                    responseObject.addProperty("status", "success");
                } else {
                    responseObject.addProperty("status", "fail");
                }
            } else {
                shoppingCart.clear();
                responseObject.addProperty("status", "success");
            }
        }

        response.getWriter().write(responseObject.toString());
    }
}
