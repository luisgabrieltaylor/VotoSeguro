/**
 *

 */
package voto.ado.sainthannaz.votoseguro.aplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import voto.ado.sainthannaz.votoseguro.R;
import voto.ado.sainthannaz.votoseguro.app.AppController;
import voto.ado.sainthannaz.votoseguro.helper.SQLiteHandler;
import voto.ado.sainthannaz.votoseguro.helper.SessionManager;


public class RegisterCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegisterCodeActivity.class.getSimpleName();
    private Button btnDate, btnRegister;
    private TextView datePicker;
    private FormEditText inputERPCO, inputDriver, inputBus, inputDate, inputSite;
    private String region, category, classification, type, suggestion, site, userIP;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private Spinner regionSpinner, categorySpinner, classificationSpinner, typeSpinner, siteSpinner;
    private DatePickerDialog.Builder builder;
    private int mYear, mMonth, mDay;
    private String[] regionArray, categoryArray, classificationArray, typeArray, siteArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_code);

        inputDate = (FormEditText)findViewById(R.id.datePicker);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        inputERPCO = (FormEditText) findViewById(R.id.code);
        inputDriver = (FormEditText) findViewById(R.id.driver);
        inputBus = (FormEditText) findViewById(R.id.bus);
        btnDate = (Button) findViewById(R.id.btnDate);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        btnDate.setOnClickListener(this);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        userIP = user.get("ip");

        regionArray = getResources().getStringArray(R.array.region);
        regionSpinner = (Spinner) findViewById(R.id.region);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.region, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        regionSpinner.setAdapter(adapter);
        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int index = arg0.getSelectedItemPosition();
                region = regionSpinner.getSelectedItem().toString();
                Toast.makeText(getBaseContext(),
                        "Región : " + regionArray[index],
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        categoryArray = getResources().getStringArray(R.array.category);
        categorySpinner = (Spinner) findViewById(R.id.category);
        ArrayAdapter<CharSequence> adapterCategory = ArrayAdapter.createFromResource(this,
                R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapterCategory);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int index = arg0.getSelectedItemPosition();
                category = categorySpinner.getSelectedItem().toString();
                Toast.makeText(getBaseContext(),
                        "Grado : " + categoryArray[index],
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        classificationArray = getResources().getStringArray(R.array.classification);
        classificationSpinner = (Spinner) findViewById(R.id.classification);
        ArrayAdapter<CharSequence> adapterClass = ArrayAdapter.createFromResource(this,
                R.array.classification, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classificationSpinner.setAdapter(adapterClass);
        classificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int index = arg0.getSelectedItemPosition();
                classification = classificationSpinner.getSelectedItem().toString();
                Toast.makeText(getBaseContext(),
                        "Clasificación : " + classificationArray[index],
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        siteArray = getResources().getStringArray(R.array.site);
        siteSpinner = (Spinner) findViewById(R.id.site);
        ArrayAdapter<CharSequence> adapterSite = ArrayAdapter.createFromResource(this,
                R.array.site, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        siteSpinner.setAdapter(adapterSite);
        siteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int index = arg0.getSelectedItemPosition();
                site = siteSpinner.getSelectedItem().toString();
                Toast.makeText(getBaseContext(),
                        "Tipo : " + typeArray[index],
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        typeArray = getResources().getStringArray(R.array.type);
        typeSpinner = (Spinner) findViewById(R.id.type);
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapterType);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int index = arg0.getSelectedItemPosition();
                type = typeSpinner.getSelectedItem().toString();
                Toast.makeText(getBaseContext(),
                        "Tipo : " + typeArray[index],
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // Register Button Click event

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String erpco = inputERPCO.getText().toString().trim();
                String driver = inputDriver.getText().toString().trim();
                String bus = inputBus.getText().toString().trim();
                String date = inputDate.getText().toString().trim();
                //String site = inputSite.getText().toString().trim();
                //String suggestion = inputSuggestion.getText().toString().trim();

                boolean allValid = true;
                FormEditText[] allFields    = {inputERPCO, inputDriver, inputBus, inputDate};
                for (FormEditText field: allFields) {
                    allValid = field.testValidity() && allValid;
                }
                //
                if (allValid) {
                    if (classification.equals("Golpeamos") && type.equals("De frente") ){
                        suggestion = "Con responsabilidad-Pago de daños";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Golpeamos") && type.equals("Por alcance") ){
                        suggestion = "Con responsabilidad-Rescisión de contrato";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Golpeamos") && type.equals("De costado") ){
                        suggestion = "Con responsabilidad-Pago de daños";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Golpeamos") && type.equals("De reversa") ){
                        suggestion = "Con responsabilidad-Pago de daños";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Golpeamos") && type.equals("Atropellamiento") ){
                        suggestion = "Con responsabilidad-Pago de daños";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Golpeamos") && type.equals("A semoviente") ){
                        suggestion = "Con responsabilidad-Pago de daños";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Golpeamos") && type.equals("En maniobras") ){
                        suggestion = "Con responsabilidad-Pago de daños";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Nos golpearon")){
                        suggestion = "Sin responsabilidad";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Golpe contra rama")){
                        suggestion = "Sin responsabilidad";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Golpe contra ave")){
                        suggestion = "Sin responsabilidad";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Apedreado")){
                        suggestion = "Sin responsabilidad";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Secuestro")){
                        suggestion = "Sin responsabilidad";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Asalto")){
                        suggestion = "Sin responsabilidad";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Incendio")){
                        suggestion = "Sin responsabilidad";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                    if (classification.equals("Falla electrica/mecanica")){
                        suggestion = "Sin responsabilidad";
                        /*Toast.makeText(getBaseContext(),
                                "La sugerencia sera: " + suggestion,
                                Toast.LENGTH_SHORT).show();*/
                    }

                    registerCode(erpco, driver, bus, category, region, date, site, classification, type, suggestion, userIP);
                } else {
                    // De lo contrario se muestra un toast indicando que los datos son incorrectos
                    /*Toast.makeText(getBaseContext(),
                            getString(R.string.error_credentials), Toast.LENGTH_SHORT)
                            .show();*/
                }

            }
        });
    }

    private void registerCode(final String erpco, final String driver,
                              final String bus, final String category, final String region, final String date, final String site,
                              final String classification, final String type, final String suggestion, final String userIP) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registrando código ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                "http://" + userIP + "/android_login_api/registerCode.php", new Response.Listener<String>() {
                //AppConfig.URL_REGISTER_CODE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONObject incident = jObj.getJSONObject("incident");
                            String erpco = incident.getString("erpco");
                            String driver = incident.getString("driver");
                            String bus = incident.getString("bus");
                            String category = incident.getString("category");
                            String region = incident.getString("region");
                            String date = incident.getString("date");
                            String site = incident.getString("site");
                            String classification = incident.getString("classification");
                            String type = incident.getString("type");
                            String suggestion = incident.getString("suggestion");
                            String createdat = incident.getString("createdat");

                        Toast.makeText(getApplicationContext(), "¡Código registrado exitosamente!", Toast.LENGTH_LONG).show();


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
                params.put("erpco", erpco);
                params.put("driver", driver );
                params.put("bus", bus);
                params.put("category", category);
                params.put("region", region);
                params.put("date", date);
                params.put("site", site);
                params.put("classification", classification);
                params.put("type", type);
                params.put("suggestion", suggestion);
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
    public void onClick(View v) {

        if (v == btnDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(this, R.style.DialogTheme,
                    new android.app.DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(android.widget.DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            inputDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            //inputDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }



    }
}
