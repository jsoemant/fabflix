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
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();
            String query = "SELECT \n" +
                    "    M.id, \n" +
                    "    M.title,\n" +
                    "    M.year,\n" +
                    "    M.director,\n" +
                    "    (SELECT \n" +
                    "            GROUP_CONCAT(sub_query.name)\n" +
                    "        FROM\n" +
                    "            (SELECT \n" +
                    "                G.name\n" +
                    "            FROM\n" +
                    "                genres G, genres_in_movies GM, movies TM\n" +
                    "            WHERE\n" +
                    "                TM.id = M.id AND M.id = GM.movieId\n" +
                    "                    AND GM.genreId = G.id) AS sub_query) AS genres,\n" +
                    "    (SELECT \n" +
                    "            GROUP_CONCAT(sub_query.name)\n" +
                    "        FROM\n" +
                    "            (SELECT \n" +
                    "                S.name\n" +
                    "            FROM\n" +
                    "                stars S, stars_in_movies SM, movies TM\n" +
                    "            WHERE\n" +
                    "                TM.id = M.id AND M.id = SM.movieId\n" +
                    "                    AND SM.starId = S.id\n" +
                    "            LIMIT 3) AS sub_query) AS stars,\n" +
                    "    (SELECT \n" +
                    "            GROUP_CONCAT(sub_query.id)\n" +
                    "        FROM\n" +
                    "            (SELECT \n" +
                    "                S.id\n" +
                    "            FROM\n" +
                    "                stars S, stars_in_movies SM, movies TM\n" +
                    "            WHERE\n" +
                    "                TM.id = M.id AND M.id = SM.movieId\n" +
                    "                    AND SM.starId = S.id\n" +
                    "            LIMIT 3) AS sub_query) AS stars_id,\n" +
                    "    R.rating\n" +
                    "FROM\n" +
                    "    ratings R,\n" +
                    "    movies M\n" +
                    "WHERE\n" +
                    "    R.movieId = M.id\n" +
                    "ORDER BY R.Rating DESC\n" +
                    "LIMIT 20";

            ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();

            while(rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");

                JsonArray genresArray = new JsonArray();
                String[] genres = rs.getString("genres").split(",");
                for (String genre: genres) {
                    genresArray.add(genre);
                }

                JsonArray starsArray = new JsonArray();
                String stars[] = rs.getString("stars").split(",");
                for (String star: stars) {
                    starsArray.add(star);
                }

                JsonArray starsIdArray = new JsonArray();
                String starsId[] = rs.getString("stars_id").split(",");
                for (String starId: starsId) {
                    starsIdArray.add(starId);
                }

                String rating = rs.getString("rating");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.add("genres", genresArray);
                jsonObject.add("stars", starsArray);
                jsonObject.add("stars_id", starsIdArray);
                jsonObject.addProperty("rating", rating);

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