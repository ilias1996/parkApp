package com.example.ilias.finalapplication.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ilias.finalapplication.AppConfig;
import com.example.ilias.finalapplication.AppController;
import com.example.ilias.finalapplication.R;
import com.example.ilias.finalapplication.SQLiteHandler;
import com.example.ilias.finalapplication.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        session.setLogin(false);
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                  /* Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("Naam","test 1 ");
                    intent.putExtra("Email","test1@hotmail.com");
                    intent.putExtra("Id","2");
                    startActivity(intent);*/
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        Log.d(TAG, "checkLogin: checking");
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {
            
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d(TAG, "onResponse: "+ jObj.getBoolean("error"));
                    // Check for error node in json
                   if (error == false) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                      //  JSONArray jArray = jObj.getJSONArray("error");

                      //  Log.d(TAG, "onResponse: " + jArray.getString(0));
                        Log.d(TAG, "onResponse: " + jObj.toString());

                     //   for (int i=0; i < jArray.length(); i++) {
                            // Now store the user in SQLite
                          //  String uid = jObj.getString("uid");

                       //     JSONObject user = jArray.getJSONObject(i);
                            String name = jObj.getString("Naam");
                            String email = jObj.getString("Email");
                            String id =  jObj.getString("Id");
                           // String created_at = user.getString("created_at");

                            // Inserting row in users table
                            //db.addUser(name, email);

                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            intent.putExtra("Naam",name);
                            intent.putExtra("Email",email);
                            intent.putExtra("Id",id);
                            startActivity(intent);
                            finish();

                        //}
                        // Launch main activity


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                   }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.d(TAG, "JSONException " + e.toString());
                    Toast.makeText(getApplicationContext(), "Json error from login: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("Email", email);
                params.put("Paswoord", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}