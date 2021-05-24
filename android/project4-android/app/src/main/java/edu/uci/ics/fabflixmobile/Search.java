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

import java.util.HashMap;
import java.util.Map;

public class Search extends ActionBarActivity {

    private EditText title;
    private TextView message;
    private Button searchButton;
    private JSONArray moviesj;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "18.216.3.125";
    private final String port = "8443";
    private final String domain = "fabflix";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.search);
        title = findViewById(R.id.title);
        searchButton = findViewById(R.id.search);
        message = findViewById(R.id.message);
        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> search());
    }

    public void search() {
        message.setText("Trying to search");

        String text = title.getText().toString();

        if (text.isEmpty()){
            String msg = "Search was empty";
            message.setText(msg);
            return;
        }
        String msg = "Searching...";
        message.setText(msg);
        Intent listPage = new Intent(Search.this, ListViewActivity.class);
        listPage.putExtra("title", title.getText().toString());
        listPage.putExtra("offset", 0);
        startActivity(listPage);
    }
}