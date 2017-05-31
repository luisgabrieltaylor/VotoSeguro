package voto.ado.sainthannaz.votoseguro.aplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rey.material.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import voto.ado.sainthannaz.votoseguro.R;
import voto.ado.sainthannaz.votoseguro.app.AppController;
import voto.ado.sainthannaz.votoseguro.helper.SQLiteHandler;
import voto.ado.sainthannaz.votoseguro.helper.SessionManager;

/**
 * Created by SaintHannaz on 30/05/2016.
 */
public class ActivityVote extends AppCompatActivity{

    private static final String TAG = ActivityVote.class.getSimpleName();
    private FormEditText inputCode;
    private ProgressDialog pDialog;
    private Button btnVote;
    private SQLiteHandler db;
    private SessionManager session;
    private String code, email_vote, name;
    private String erpcoCode, driverCode, busCode, categoryCode, regionCode, dateCode, siteCode, classificationCode, typeCode, suggestionCode, statusCode, createdatCode, userIP;
    private Boolean votingValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        inputCode = (FormEditText) findViewById(R.id.incident_code);
        btnVote = (Button) findViewById(R.id.btnVote);

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        name = user.get("name");
        email_vote = user.get("email");
        userIP = user.get("ip");

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        btnVote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                code = inputCode.getText().toString().trim();
                boolean allValid = true;
                FormEditText[] allFields    = {inputCode};
                for (FormEditText field: allFields) {
                    allValid = field.testValidity() && allValid;
                }
                //
                if (allValid) {
                    checkCode(code, userIP);
                } else {
                    Toast.makeText(getBaseContext(),
                            getString(R.string.error_code), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void checkCode(final String erpco, final String userIP) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Checando codigo...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                "http://" + userIP + "/android_login_api/getCode.php", new Response.Listener<String>() {
                //AppConfig.URL_GET_CODE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Code Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONObject incident = jObj.getJSONObject("incident");
                        erpcoCode = incident.getString("erpco");
                        driverCode = incident.getString("driver");
                        busCode = incident.getString("bus");
                        categoryCode = incident.getString("category");
                        regionCode = incident.getString("region");
                        dateCode = incident.getString("date");
                        siteCode = incident.getString("site");
                        classificationCode = incident.getString("classification");
                        typeCode = incident.getString("type");
                        suggestionCode = incident.getString("suggestion");
                        statusCode = incident.getString("status");
                        createdatCode = incident.getString("createdat");
                        if (statusCode.equals("Abierto")) {
                            checkVoting(code, email_vote, userIP);
                        }
                        if (statusCode.equals("Cerrado")) {
                            Toast.makeText(getApplicationContext(),
                                    "Código cerrado, ya no es posible votar", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                     // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error en los datos del codigo: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("erpco", erpco);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void checkVoting(final String code_vote, final String email_vote, final String userIP) {
        // Tag used to cancel the request
        String tag_string_reqVOTE = "req_vote";

        StringRequest strReq = new StringRequest(Method.POST,
                "http://" + userIP + "/android_login_api/getVote.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Vote Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        JSONObject voting = jObj.getJSONObject("voting");
                        String code_vote = voting.getString("code_vote");
                        String email_vote = voting.getString("email_vote");
                        Toast.makeText(getApplicationContext(), "Usted ya voto este código!", Toast.LENGTH_LONG).show();
                        votingValue = true;
                        Log.d(TAG, "Boolean value: " + votingValue.toString());
                        finish();


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Preparando datos para votación", Toast.LENGTH_LONG).show();
                        votingValue = false;
                        Log.d(TAG, "Boolean value: " + votingValue.toString());
                        Intent explicit_intent = new Intent(ActivityVote.this, ActivityVoting.class);
                        explicit_intent.putExtra("erpco", erpcoCode);
                        explicit_intent.putExtra("bus", busCode);
                        explicit_intent.putExtra("category", categoryCode);
                        explicit_intent.putExtra("region", regionCode);
                        explicit_intent.putExtra("date", dateCode);
                        explicit_intent.putExtra("site", siteCode);
                        explicit_intent.putExtra("classification", classificationCode);
                        explicit_intent.putExtra("type", typeCode);
                        explicit_intent.putExtra("suggestion", suggestionCode);
                        startActivity(explicit_intent);

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error en los parametros del código " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("code_vote", code_vote);
                params.put("email_vote", email_vote);
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_reqVOTE);
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
