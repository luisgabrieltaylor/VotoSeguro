/**
 *

 */
package voto.ado.sainthannaz.votoseguro.aplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rey.material.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import voto.ado.sainthannaz.votoseguro.R;
import voto.ado.sainthannaz.votoseguro.app.AppConfig;
import voto.ado.sainthannaz.votoseguro.app.AppController;
import voto.ado.sainthannaz.votoseguro.helper.SQLiteHandler;
import voto.ado.sainthannaz.votoseguro.helper.SessionManager;

public class RegisterUserActivity extends Activity implements Spinner.OnItemSelectedListener {

    private static final String TAG = RegisterUserActivity.class.getSimpleName();
    private Button btnRegister;
    private FormEditText inputFullName;
    private FormEditText inputEmail;
    private EditText inputPassword;
    private Spinner SpinnerMAC, SpinnerRegion, levelSpinner;
    //private EditText inputPassword2;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;
    private static final int MY_PASSWORD_DIALOG_ID = 4;
    private String[] levelArray;
    private String level, MACUser, RegionUser, userIP;
    //JSON Array
    private JSONArray resultRegion;
    private JSONArray resultMAC;
    //An ArrayList for Spinner Items
    private ArrayList<String> deviceMAC;
    private ArrayList<String> userRegion;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        inputFullName = (FormEditText) findViewById(R.id.name);
        inputEmail = (FormEditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        SpinnerMAC = (Spinner) findViewById(R.id.spinnerMAC);
        SpinnerRegion = (Spinner) findViewById(R.id.spinnerRegion);

        //Initializing the ArrayList and adding an Item Selected Listener to our Spinner
        deviceMAC= new ArrayList<String>();
        SpinnerMAC.setOnItemSelectedListener(this);
        userRegion= new ArrayList<String>();
        SpinnerRegion.setOnItemSelectedListener(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        userIP = user.get("ip");
        System.out.println(userIP);

        getDataMAC(userIP);
        getDataRegion(userIP);

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                //String password2 = inputPassword2.getText().toString().trim();
                boolean allValid = true;
                FormEditText[] allFields    = { inputFullName, inputEmail };
                for (FormEditText field: allFields) {
                    allValid = field.testValidity() && allValid;
                }
                if (allValid) {
                    if (!password.isEmpty()) {
                        registerUser(name, email, MACUser, RegionUser, level, password, userIP);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Ingrese y confirme el password!", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    // De lo contrario se muestra un toast indicando que los datos son incorrectos
                    Toast.makeText(getBaseContext(),
                            getString(R.string.error_credentials), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        levelArray = getResources().getStringArray(R.array.level);
        levelSpinner = (Spinner) findViewById(R.id.level);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        levelSpinner.setAdapter(adapter);
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int index = arg0.getSelectedItemPosition();
                level = levelSpinner.getSelectedItem().toString();
                //Toast.makeText(getBaseContext(),
                        //"Level : " + levelArray[index],
                        //Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void getDataMAC(String userIP){
        //Creating a string request
        StringRequest stringRequest = new StringRequest("http://"+ userIP + "/android_login_api/getMAC.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            resultMAC = j.getJSONArray(AppConfig.JSON_ARRAY_MAC);

                            //Calling method getStudents to get the students from the JSON Array
                            getMAC(resultMAC);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getDataRegion(String userIP){
        //Creating a string request
        //StringRequest stringRequest = new StringRequest(AppConfig.URL_GET_REGION,
        StringRequest stringRequest = new StringRequest("http://" + userIP + "/android_login_api/getRegion.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            resultRegion = j.getJSONArray(AppConfig.JSON_ARRAY_REGION);

                            //Calling method getStudents to get the students from the JSON Array
                            getRegion(resultRegion);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getMAC(JSONArray j){
        //Traversing through all the items in the json array
        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                deviceMAC.add(json.getString(AppConfig.TAG_MAC));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Setting adapter to show the items in the spinner
        SpinnerMAC.setAdapter(new ArrayAdapter<String>(RegisterUserActivity.this, android.R.layout.simple_spinner_dropdown_item, deviceMAC));
    }

    private void getRegion(JSONArray j){
        //Traversing through all the items in the json array
        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                userRegion.add(json.getString(AppConfig.TAG_REGION));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Setting adapter to show the items in the spinner
        SpinnerRegion.setAdapter(new ArrayAdapter<String>(RegisterUserActivity.this, android.R.layout.simple_spinner_dropdown_item, userRegion));
    }

    private String getDeviceMAC(int position){
        String mac="";
        try {
            //Getting object of given index
            JSONObject json = resultMAC.getJSONObject(position);

            //Fetching name from that object
            mac = json.getString(AppConfig.TAG_MAC);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return mac;
    }

    private String getUserRegion(int position){
        String region="";
        try {
            //Getting object of given index
            JSONObject json = resultRegion.getJSONObject(position);

            //Fetching name from that object
            region = json.getString(AppConfig.TAG_REGION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return region;
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name, final String email, final String usermac, final String region, final String level,
                              final String password, final String userIP) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registrando ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                "http://" + userIP + "/android_login_api/registerUser.php", new Response.Listener<String>() {
                //AppConfig.URL_REGISTER_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String usermac = user.getString("usermac");
                        String region = user.getString("region");
                        String level = user.getString("level");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                       // db.addUser(name, email, level, id,  uid, created_at);

                        Toast.makeText(getApplicationContext(), "¡Usuario registrado exitosamente!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterUserActivity.this,
                                ActivityLogin.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("usermac", usermac);
                params.put("region", region);
                params.put("level", level);
                params.put("password", password);
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinnerMAC:
                //Setting the values to textviews for a selected item
                MACUser = getDeviceMAC(position);
                Toast.makeText(getApplicationContext(), getDeviceMAC(position), Toast.LENGTH_LONG).show();

                break;
            case R.id.spinnerRegion:
                RegionUser = getUserRegion(position);
                Toast.makeText(getApplicationContext(), getUserRegion(position), Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
