/**
 *

 */
package voto.ado.sainthannaz.votoseguro.aplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
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


public class RegisterDeviceActivity extends Activity {

    private static final String TAG = RegisterDeviceActivity.class.getSimpleName();
    private Button btnRegister;
    private FormEditText mMacEdit;
    private FormEditText mName;
    private FormEditText mUser;
    private FormEditText inputDeviceName;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private String userIP;
    private TextInputLayout inputLayoutName, inputLayoutMAC, inputLayoutUser;
    private static final int MY_PASSWORD_DIALOG_ID = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device);

        mMacEdit = (FormEditText) findViewById(R.id.deviceMAC);
        mName = (FormEditText) findViewById (R.id.deviceName);
        mUser = (FormEditText) findViewById (R.id.userName);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_devicename);
        inputLayoutMAC = (TextInputLayout) findViewById(R.id.input_layout_mac);
        inputLayoutUser = (TextInputLayout) findViewById(R.id.input_layout_user);
        //btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

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

        registerAfterMacTextChangedCallback();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = mName.getText().toString().trim();
                String mac = mMacEdit.getText().toString().trim();
                String user = mUser.getText().toString().trim();
                //String password2 = inputPassword2.getText().toString().trim();
                boolean allValid = true;
                FormEditText[] allFields    = { mName, mMacEdit, mUser  };
                for (FormEditText field: allFields) {
                    allValid = field.testValidity() && allValid;
                }
                // Si los valores en los campo son validos se procede al logeo en el sistema
                if (allValid) {
                    if (!mac.isEmpty()) {
                        registerDevice(name, mac, user, userIP);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Confirme los datos!", Toast.LENGTH_LONG)
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
    }

    private void registerDevice(final String name, final String mac,
                              final String user, final String userIP) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registrando ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                "http://"+ userIP + "/android_login_api/registerDevice.php", new Response.Listener<String>() {
            //AppConfig.URL_REGISTER_DEVICE, new Response.Listener<String>() {

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

                        JSONObject device = jObj.getJSONObject("device");
                        String name = device.getString("name");
                        String mac = device.getString("mac");
                        String user = device.getString("user");
                        String created_at = device
                                .getString("created_at");

                        // Inserting row in users table
                        //db.addDevice(name, email, uid, created_at);

                        Toast.makeText(getApplicationContext(), "¡Dispositivo registrado exitosamente!", Toast.LENGTH_LONG).show();


                        //finish();
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
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("mac", mac );
                params.put("user", user);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void registerAfterMacTextChangedCallback() {
        mMacEdit.addTextChangedListener(new TextWatcher() {
            String mPreviousMac = null;

            /* (non-Javadoc)
             * Does nothing.
             * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
             */
            @Override
            public void afterTextChanged(Editable arg0) {
            }

            /* (non-Javadoc)
             * Does nothing.
             * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
             */
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            /* (non-Javadoc)
         * Formats the MAC address and handles the cursor position.
         * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
         */
            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredMac = mMacEdit.getText().toString().toUpperCase();
                String cleanMac = clearNonMacCharacters(enteredMac);
                String formattedMac = formatMacAddress(cleanMac);

                int selectionStart = mMacEdit.getSelectionStart();
                formattedMac = handleColonDeletion(enteredMac, formattedMac, selectionStart);
                int lengthDiff = formattedMac.length() - enteredMac.length();

                setMacEdit(cleanMac, formattedMac, selectionStart, lengthDiff);
            }

            /**
             * Strips all characters from a string except A-F and 0-9.
             *
             * @param mac User input string.
             * @return String containing MAC-allowed characters.
             */
            private String clearNonMacCharacters(String mac) {
                return mac.toString().replaceAll("[^A-Fa-f0-9]", "");
            }

            /**
             * Adds a colon character to an unformatted MAC address after
             * every second character (strips full MAC trailing colon)
             *
             * @param cleanMac Unformatted MAC address.
             * @return Properly formatted MAC address.
             */
            private String formatMacAddress(String cleanMac) {
                int grouppedCharacters = 0;
                String formattedMac = "";

                for (int i = 0; i < cleanMac.length(); ++i) {
                    formattedMac += cleanMac.charAt(i);
                    ++grouppedCharacters;

                    if (grouppedCharacters == 2) {
                        formattedMac += ":";
                        grouppedCharacters = 0;
                    }
                }

                // Removes trailing colon for complete MAC address
                if (cleanMac.length() == 12)
                    formattedMac = formattedMac.substring(0, formattedMac.length() - 1);

                return formattedMac;
            }

            /**
             * Upon users colon deletion, deletes MAC character preceding deleted colon as well.
             *
             * @param enteredMac     User input MAC.
             * @param formattedMac   Formatted MAC address.
             * @param selectionStart MAC EditText field cursor position.
             * @return Formatted MAC address.
             */
            private String handleColonDeletion(String enteredMac, String formattedMac, int selectionStart) {
                if (mPreviousMac != null && mPreviousMac.length() > 1) {
                    int previousColonCount = colonCount(mPreviousMac);
                    int currentColonCount = colonCount(enteredMac);

                    if (currentColonCount < previousColonCount) {
                        formattedMac = formattedMac.substring(0, selectionStart - 1) + formattedMac.substring(selectionStart);
                        String cleanMac = clearNonMacCharacters(formattedMac);
                        formattedMac = formatMacAddress(cleanMac);
                    }
                }
                return formattedMac;
            }

            /**
             * Gets MAC address current colon count.
             *
             * @param formattedMac Formatted MAC address.
             * @return Current number of colons in MAC address.
             */
            private int colonCount(String formattedMac) {
                return formattedMac.replaceAll("[^:]", "").length();
            }

            /**
             * Removes TextChange listener, sets MAC EditText field value,
             * sets new cursor position and re-initiates the listener.
             *
             * @param cleanMac       Clean MAC address.
             * @param formattedMac   Formatted MAC address.
             * @param selectionStart MAC EditText field cursor position.
             * @param lengthDiff     Formatted/Entered MAC number of characters difference.
             */
            private void setMacEdit(String cleanMac, String formattedMac, int selectionStart, int lengthDiff) {
                mMacEdit.removeTextChangedListener(this);
                if (cleanMac.length() <= 12) {
                    mMacEdit.setText(formattedMac);
                    mMacEdit.setSelection(selectionStart + lengthDiff);
                    mPreviousMac = formattedMac;
                } else {
                    mMacEdit.setText(mPreviousMac);
                    mMacEdit.setSelection(mPreviousMac.length());
                }
                mMacEdit.addTextChangedListener(this);
            }
        });
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
