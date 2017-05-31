package voto.ado.sainthannaz.votoseguro.aplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rey.material.widget.Button;
import com.rey.material.widget.SnackBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import voto.ado.sainthannaz.votoseguro.R;
import voto.ado.sainthannaz.votoseguro.app.AppController;
import voto.ado.sainthannaz.votoseguro.app.CustomList;
import voto.ado.sainthannaz.votoseguro.app.ParseJSON;
import voto.ado.sainthannaz.votoseguro.helper.SQLiteHandler;
import voto.ado.sainthannaz.votoseguro.helper.SessionManager;

/**
 * Created by SaintHannaz on 25/08/2016.
 */
public class ActivityResumeCode extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = ActivityResumeCode.class.getSimpleName();
    private String code, bus, category, date, site, classification, type, suggestion, vote, email_vote, codeResult, userIP;
    private TextView codeView, busView, dateView, rc, pd, sr;
    private ProgressDialog pDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private Button refreshCode, closeCode, clearCode;
    private SQLiteHandler db;
    private SessionManager session;
    public String[] votes;
    final Context context = this;
    private CoordinatorLayout coordinatorLayout;
    private SnackBar mSnackBar;
    int SR, PD, RC ;
    String [] initialArray;
    public static final String JSON_ARRAY = "result";
    public static final String KEY_ID = "id";
    public static final String KEY_CODEVOTE = "code_vote";
    public static final String KEY_VOTE = "vote";
    public static final String KEY_DATEVOTE = "date_vote";

    public static JSONArray allVotes = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_code);

        final View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        final View coordinatorLayoutView = findViewById(R.id.coordinatorLayout);

        refreshCode = (Button) findViewById(R.id.refreshCode);
        refreshCode.setOnClickListener(this);
        closeCode = (Button) findViewById(R.id.closeCode);
        clearCode = (Button) findViewById(R.id.clearCode);
        codeView = (TextView) findViewById(R.id.erpco);
        busView = (TextView) findViewById(R.id.bus);
        dateView = (TextView) findViewById(R.id.date);
        listView = (ListView) findViewById(R.id.listView);
        rc = (TextView) findViewById(R.id.rc);
        pd = (TextView) findViewById(R.id.pd);
        sr = (TextView) findViewById(R.id.sr);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        userIP = user.get("ip");

        Intent intent=getIntent();
        Bundle extras =intent.getExtras();
        if (extras != null) {//ver si contiene datos
            code = (String) extras.get("erpco");//Codigo ERPCO
            bus = (String) extras.get("bus");//Bus
            category = (String) extras.get("category");//Bus
            date = (String) extras.get("date");//Bus
            site = (String) extras.get("site");//Bus
            classification = (String) extras.get("classification");//Bus
            type = (String) extras.get("type");//Bus
            suggestion = (String) extras.get("suggestion");//Bus
        }

        codeView.setText(getString(R.string.txt_codesession) + " " + code);
        busView.setText(getString(R.string.txt_bus) + " " + bus);
        dateView.setText(getString(R.string.txt_codedate) + " " + date);

        // add button listener
        closeCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_closevote);
                sendRequest(code, userIP);
                dialog.setTitle("¡Advertencia!");
                Button cancelButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
                Button okButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                Button vsButton = (Button) dialog.findViewById(R.id.dialogButtonVS);

                TextView aboutD = (TextView) dialog.findViewById(R.id.about_txt);
                TextView erpcoD = (TextView) dialog.findViewById(R.id.erpco_txt);
                TextView busD = (TextView) dialog.findViewById(R.id.bus_txt);
                TextView categoryD = (TextView) dialog.findViewById(R.id.category_txt);
                TextView dateD = (TextView) dialog.findViewById(R.id.date_txt);
                TextView siteD = (TextView) dialog.findViewById(R.id.site_txt);
                TextView classificationD = (TextView) dialog.findViewById(R.id.classification_txt);
                TextView typeD = (TextView) dialog.findViewById(R.id.type_txt);


                erpcoD.setText("Código: " + code);
                busD.setText("Autobus: " + bus);
                categoryD.setText("Categoria: " + category);
                dateD.setText("Fecha: " + date);
                siteD.setText("Lugar del incidente: " + site);
                classificationD.setText("Clasificación: " + classification);
                typeD.setText("Tipo: " + type);

                // if button is clicked, close the custom dialog
                if(codeResult.equals("Empate")){
                    aboutD.setText("Empate");
                    Toast.makeText(ActivityResumeCode.this,"Existe un empate, verifique los votos o proceda al voto de confianza",Toast.LENGTH_LONG).show();
                    // if button is clicked, close the custom dialog

                    vsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendRequest(code, userIP);
                            dialog.dismiss();
                        }
                    });
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendRequest(code, userIP);
                            dialog.dismiss();
                        }
                    });
                } else {
                    aboutD.setText(codeResult);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //closeCode(code, email_vote, vote, "votado");

                            closeCode(code, codeResult, userIP);
                            dialog.dismiss();
                            Snackbar
                                    .make(coordinatorLayoutView, "Este codigo a sido cerrado exitosamente", Snackbar.LENGTH_LONG)
                                    .setAction(R.string.snackbar_action_ok, clickListener)
                                    .show();
                            final Handler handler = new Handler();
                            Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                public void run() {
                                    handler.post(new Runnable() {
                                        public void run() {
                                            finish();
                                        }
                                    });
                                }
                            }, 2000);
                        }

                    });


                    // if button is clicked, close the custom dialog
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendRequest(code, userIP);
                            dialog.dismiss();
                        }
                    });
                }




                dialog.show();
            }
        });

        clearCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        sendRequest(code, userIP);
                                    }
                                }
        );


    }

    private void sendRequest(final String erpco, final String userIP){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + userIP + "/android_login_api/getVotes.php",
                //StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_VOTES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            allVotes = jObj.getJSONArray(JSON_ARRAY);
                            initialArray = new String[allVotes.length()];
                            votes = new String[allVotes.length()];
                            for(int i=0;i<allVotes.length();i++){
                                JSONObject jo = allVotes.getJSONObject(i);
                                votes[i] = jo.getString(KEY_VOTE);
                                initialArray[i] = votes[i] = jo.getString(KEY_VOTE);
                                System.out.println(initialArray[i]);
                            }
                            System.out.println(Arrays.toString(initialArray));
                            SR = 0; PD = 0; RC = 0;
                            for(int j=0;j<initialArray.length;j++) {
                                if (initialArray[j].equals("Con responsabilidad-Pago de daños")){
                                    PD = PD + 1;
                                }
                                if (initialArray[j].equals("Sin responsabilidad")){
                                    SR = SR + 1;
                                }
                                if (initialArray[j].equals("Con responsabilidad-Rescisión de contrato")){
                                    RC = RC + 1;
                                }
                            }
                            System.out.println(SR);System.out.println(PD);System.out.println(RC);
                            pd.setText(String.valueOf(PD));
                            sr.setText(String.valueOf(SR));
                            rc.setText(String.valueOf(RC));

                            if ( PD > SR && PD > RC ){
                                System.out.println("Con responsabilidad-Pago de daños");
                                codeResult = "Con responsabilidad-Pago de daños";
                            }
                            else if ( SR > PD && SR > RC ) {
                                System.out.println("Sin responsabilidad");
                                codeResult = "Sin responsabilidad";
                            }
                            else if ( RC > PD && RC > SR ) {
                                System.out.println("Con responsabilidad-Rescisión de contrato");
                                codeResult = "Con responsabilidad-Rescisión de contrato";
                            }
                            else {
                                System.out.println("Empate");
                                codeResult = "Empate";
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        showJSON(response);
                        System.out.println(codeResult);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ActivityResumeCode.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);
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


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        // stopping swipe refresh

        swipeRefreshLayout.setRefreshing(false);
    }

    private void closeAndCheckCode(final String erpco)
    {

    }
    private void closeCode(final String erpco, final String result, final String userIP) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        //pDialog.setMessage("Cerrando código ...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://" + userIP + "/android_login_api/closeCode.php", new Response.Listener<String>() {
                //AppConfig.URL_CLOSE_CODE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONObject incident = jObj.getJSONObject("incident");
                        String erpco = incident.getString("erpco");
                        String result = incident.getString("result");
                        Toast.makeText(getApplicationContext(), "¡Código cerrado exitosamente!", Toast.LENGTH_LONG).show();
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
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("erpco", erpco);
                params.put("result", result);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showJSON(String json){
        ParseJSON pj = new ParseJSON(json);
        pj.parseJSON();
        CustomList cl = new CustomList(this, ParseJSON.ids,ParseJSON.code_votes,ParseJSON.votes, ParseJSON.date_votes);
        listView.setAdapter(cl);
    }

    @Override
    public void onClick(View v) {
        sendRequest(code, userIP);
    }

    @Override
    public void onRefresh() {
        sendRequest(code, userIP);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    public String[] addToArrayString(String[] initalArray, String newValue)
    {
        String[] returnArray = new String[initalArray.length+1];
        for(int i = 0; i < initalArray.length; i++)
        {
            returnArray[i] = initalArray[i];
        }
        returnArray[returnArray.length-1] = newValue;

        return returnArray;
    }

    public int[] addToArrayInt(int[] initalArray, int newValue)
    {
        int[] returnArray = new int[initalArray.length+1];
        for(int i = 0; i < initalArray.length; i++)
        {
            returnArray[i] = initalArray[i];
        }
        returnArray[returnArray.length-1] = newValue;

        return returnArray;
    }
}
