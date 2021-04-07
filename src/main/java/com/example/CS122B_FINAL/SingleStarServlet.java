package com.example.CS122B_FINAL;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
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

@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String requestId = request.getParameter("id");
        PrintWriter out = response.getWriter();

        try {
            Connection dbcon = dataSource.getConnection();
//            Statement statement = dbcon.createStatement();
            String query = "SELECT \n" +
                    "    S.id,\n" +
                    "    S.name,\n" +
                    "    IFNULL(S.birthYear, 'N/A') as birthYear,\n" +
                    "    (SELECT \n" +
                    "            GROUP_CONCAT(sub_query.id)\n" +
                    "        FROM\n" +
                    "            (SELECT \n" +
                    "                M.id\n" +
                    "            FROM\n" +
                    "                stars_in_movies SM, movies M\n" +
                    "            WHERE\n" +
                    "                S.id = SM.starId AND SM.movieId = M.id) AS sub_query) AS movies_id,\n" +
                    "    (SELECT \n" +
                    "            GROUP_CONCAT(sub_query.title)\n" +
                    "        FROM\n" +
                    "            (SELECT \n" +
                    "                M.title\n" +
                    "            FROM\n" +
                    "                stars_in_movies SM, movies M\n" +
                    "            WHERE\n" +
                    "                S.id = SM.starId AND SM.movieId = M.id) AS sub_query) AS movies\n" +
                    "FROM\n" +
                    "    stars S\n" +
                    "WHERE\n" +
                    "    S.id = " + "?" ;

            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1, requestId);

            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();

            while(rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String birthYear = rs.getString("birthYear");
                JsonArray moviesArray = new JsonArray();
                String[] movies = rs.getString("movies").split(",");
                for (String movie: movies) {
                    moviesArray.add(movie);
                }

                JsonArray moviesIdArray = new JsonArray();
                String[] moviesId = rs.getString("movies_id").split(",");
                for (String Id: moviesId) {
                    moviesIdArray.add(Id);
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("name", name);
                jsonObject.addProperty("birthYear", birthYear);
                jsonObject.add("movies", moviesArray);
                jsonObject.add("moviesId", moviesIdArray);

                jsonArray.add(jsonObject);
            }

            out.write(jsonArray.toString());
            response.setStatus(200);
            rs.close();
            statement.close();
            dbcon.close();

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        out.close();
    }
}