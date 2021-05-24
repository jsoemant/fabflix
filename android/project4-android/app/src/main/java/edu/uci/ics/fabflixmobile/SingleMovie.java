package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleMovie extends ActionBarActivity {

    private EditText username;
    private EditText password;
    private TextView message;
    private Button loginButton;
    String title;
    String id;
    String director = "";
    String stars = "";
    String genres = "";
    TextView xtitle;
    TextView xsub;
    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "18.116.60.164";
    private final String port = "8443";
    private final String domain = "fabflix";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.singlemovie);
        xtitle = findViewById(R.id.title);
        xsub = findViewById(R.id.subtitle);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        getData();
    }

    public void getData() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        System.out.println("sdfhajk");
        final StringRequest loginRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/single-movie?id=" + id,
                response -> {
                    try{
                        System.out.println("sdfhajk2");
                        Log.d("s-movie.success", response);
                        JSONObject temp = new JSONObject(response);
                        director = temp.getString("director");
                        title = temp.getString("title");
                        JSONArray jstars = temp.getJSONArray("stars");
                        JSONArray jgenres = temp.getJSONArray("genres");
                        String sep = "";
                        for (int i = 0; i < jstars.length(); i++){
                            System.out.println(jstars.getJSONObject(i).getString("name"));
                            stars += sep + jstars.getJSONObject(i).getString("name");
                            sep = ", ";
                        }
                        sep = "";
                        for (int i = 0; i < jgenres.length(); i++){
                            genres += sep + jgenres.getJSONObject(i).getString("name");
                            sep = ", ";
                        }
                        xtitle.setText(title);
                        String sub = "Director: " + director +
                                "\n\nStars: " + stars + "\n\nGenres: " + genres;
                        xsub.setText(sub);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
        };

        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);

    }
}