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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends ActionBarActivity {

    private EditText username;
    private EditText password;
    private TextView message;
    private Button loginButton;

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
        setContentView(R.layout.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        message = findViewById(R.id.message);
        loginButton = findViewById(R.id.login);

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login());
        System.out.println("hello");
    }

    public void login() {

        message.setText("Trying to login");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/login",
                response -> {
                    try{
                        Log.d("login.success", response);
                        JSONObject temp = new JSONObject(response);
                        String status = temp.getString("status");
                        String msg = temp.getString("message");
                        message.setText(msg);
                        if (!status.equals("fail")){
                            System.out.println(status);
                            // initialize the activity(page)/destination
                            Intent listPage = new Intent(Login.this, Search.class);
                            // activate the list page.
                            startActivity(listPage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());

                return params;
            }
        };

        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);

    }
}