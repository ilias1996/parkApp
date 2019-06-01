package com.example.ilias.finalapplication.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ilias.finalapplication.AppConfig;
import com.example.ilias.finalapplication.AppController;
import com.example.ilias.finalapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddCarActivity extends AppCompatActivity {

    private EditText mLicensePlate;
    private EditText mKind;
    private EditText mLength;
    private Button mAddCar;
    private ProgressDialog pDialog;

    private Button goBack;
    private final String TAG = AddCarActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        mLicensePlate = (EditText) findViewById(R.id.edtLicense);
        mKind = (EditText) findViewById(R.id.edtKind);

        mLength = (EditText) findViewById(R.id.edtLength);
        mAddCar = (Button) findViewById(R.id.btnAddCar);

        goBack = (Button) findViewById(R.id.btnGoBack);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        mAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLicensePlate.getText().toString().isEmpty() && mKind.getText().toString().isEmpty() && mLength.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Fill in all the fields", Toast.LENGTH_LONG).show();
            }else{
                    addCar(mLicensePlate.getText().toString(),mKind.getText().toString(), mLength.getText().toString());
                }}
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCarActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }

    private void addCar(final String license, final String kind, final String length){
        String tag_string_req = "req_login";

        pDialog.setMessage("Register in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CREATE_CAR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Add Car Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");


                      Intent intent = new Intent(AddCarActivity.this,MainActivity.class);
                        startActivity(intent);
                         finish();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.d(TAG, "Json error from Add CAR :JSONException " + e.toString());
                    //Toast.makeText(getApplicationContext(), "Json error from Add CAR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Add Car Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("UserId", "1");
                params.put("Kind", kind);
                params.put("LicensePlate", license);
                params.put("Length", length);

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
