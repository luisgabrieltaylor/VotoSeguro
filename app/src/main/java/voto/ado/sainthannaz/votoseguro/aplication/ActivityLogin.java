package voto.ado.sainthannaz.votoseguro.aplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
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

import de.cketti.library.changelog.ChangeLog;
import voto.ado.sainthannaz.votoseguro.R;
import voto.ado.sainthannaz.votoseguro.app.AppController;
import voto.ado.sainthannaz.votoseguro.helper.SQLiteHandler;
import voto.ado.sainthannaz.votoseguro.helper.SessionManager;

/**
 * Created by Saint Hannaz on 17-Aug-15.
 */

public class ActivityLogin extends Activity  {

    private static final String TAG = RegisterUserActivity.class.getSimpleName();
    private long back_pressed;
    private String localmac;
    private Button btnLogin;
    private Button btnRegister;
    private FormEditText inputEmail;
    private FormEditText inputPassword;
    private FormEditText inputIp;
    private ProgressDialog pDialog, pDialogMAC;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_screen);

        // Al ser la primera ejecución o actualización se ejecuta el changelog
        ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun()) {
            cl.getLogDialog().show();
        }

        // Edittext
        inputEmail = (FormEditText) findViewById(R.id.email);
        inputPassword = (FormEditText) findViewById(R.id.password);
        inputIp = (FormEditText) findViewById(R.id.ipAddress);
        // Botones de Login y Registro (Este ultimo es temporal)
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Detecta la MAC del dispositivo y la muestra
        WifiManager wimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        localmac = wimanager.getConnectionInfo().getMacAddress();
        TextView mymacAdress = (TextView) findViewById(R.id.macAdress);
        mymacAdress.setText("MAC: " + localmac);

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(ActivityLogin.this, ActivityMainMenu.class);
            startActivity(intent);
            finish();
        }

        // Dialogo de proceso para el login
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                FormEditText[] allFields    = { inputEmail, inputPassword, inputIp };
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String ip = inputIp.getText().toString().trim();

                boolean allValid = true;
                for (FormEditText field: allFields) {
                    allValid = field.testValidity() && allValid;
                }
                // Si los valores en los campo son validos se procede al logeo en el sistema
                if (allValid) {
                    checkMAC(localmac, ip);
                    checkLogin(email, password, ip);
                } else {
                    // De lo contrario se muestra un toast indicando que los datos son incorrectos
                    Toast.makeText(getBaseContext(),
                            getString(R.string.error_credentials), Toast.LENGTH_SHORT)
                            .show();
                }

            }

        });

        // Link to Register Screen
        /*
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),RegisterUserActivity.class);
                startActivity(i);
                finish();
            }
        });*/


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();

        } else {
            Toast.makeText(getBaseContext(), getString(R.string.toast_PressAgain), Toast.LENGTH_SHORT)
                    .show();
        }
        back_pressed = System.currentTimeMillis();
    }


    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password, final String ip) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Checando credenciales...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                ("http://"+ ip +"/android_login_api/login.php"), new Response.Listener<String>() {
                //AppConfig.LoginConnection(ip), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String level = user.getString("level");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(name, email, level, ip ,uid, created_at);

                        // Launch main activity
                        Intent intent = new Intent(ActivityLogin.this,
                                ActivityMainMenu.class);
                        startActivity(intent);

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
                Log.e(TAG, "Error en las credenciales: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                params.put("ip", ip);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkMAC(final String mac, final String ip) {
        // Tag used to cancel the request
        String tag_string_reqMAC = "req_mac";

        //pDialog.setMessage("Checando MAC...");
        //showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                ("http://"+ ip +"/android_login_api/checkMAC.php"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        JSONObject device = jObj.getJSONObject("device");
                        String mac = device.getString("mac");
                        Toast.makeText(getApplicationContext(), "Dispositivo validado", Toast.LENGTH_LONG).show();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Dispositivo no autorizado", Toast.LENGTH_LONG).show();
                        finish();
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
                Log.e(TAG, "Error en la MAC: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("mac", mac);
                return params;            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_reqMAC);
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
