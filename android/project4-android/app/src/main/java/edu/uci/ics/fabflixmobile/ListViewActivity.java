package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ListViewActivity extends Activity {

    String title;
    int offset;
    int max;
    String id;
    private final String host = "18.216.3.125";
    private final String port = "8443";
    private final String domain = "fabflix";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;
    private Button backButton;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        offset = intent.getIntExtra("offset", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/movie-list?title=" + title + "&limit=20&offset=" + offset +
                        "&order=TITLEASCRATINGASC" ,
                response -> {
                    try {
                        Log.d("list.success", response);
                        JSONObject resp = new JSONObject(response);
                        max = resp.getInt("max");
                        System.out.println(max);
                        JSONArray moviesj = resp.getJSONArray("data");
                        final ArrayList<Movie> movies = new ArrayList<>();
                        for (int i = 0; i < moviesj.length(); i++){
                            try {
                                movies.add(new Movie(moviesj.getJSONObject(i).getString("movie_title"),
                                        (short) Integer.parseInt(moviesj.getJSONObject(i).getString("movie_year")),
                                        moviesj.getJSONObject(i).getString("movie_director"),
                                        moviesj.getJSONObject(i).getJSONArray("stars"),
                                        moviesj.getJSONObject(i).getJSONArray("genres"),
                                        moviesj.getJSONObject(i).getString("movie_id")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);
                        if ((offset - 20) >= 0){
                            System.out.println("hi");
                            backButton = findViewById(R.id.back);
                            backButton.setOnClickListener(view -> back());
                        }
                        System.out.println(offset + 20);
                        if ((offset + 20) <= max) {
                            System.out.println("hello");
                            nextButton = findViewById(R.id.next);
                            nextButton.setOnClickListener(view -> next());
                        }
                        ListView listView = findViewById(R.id.list);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Movie movie = movies.get(position);
                            System.out.println("clicked: " + movie.id);
//                            String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
//                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            Intent listPage = new Intent(ListViewActivity.this, SingleMovie.class);
                            listPage.putExtra("id", movie.id);
                            startActivity(listPage);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
                    Log.d("list.error", error.toString());
                });
        queue.add(searchRequest);
        // important: queue.add is where the login request is actually sent
    }

    public void next(){
        Intent listPage = new Intent(ListViewActivity.this, ListViewActivity.class);
        listPage.putExtra("title", title);
        listPage.putExtra("offset", offset + 20);
        startActivity(listPage);
    }
    public void back(){
        Intent listPage = new Intent(ListViewActivity.this, ListViewActivity.class);
        listPage.putExtra("title", title);
        listPage.putExtra("offset", offset - 20);
        startActivity(listPage);
    }
}