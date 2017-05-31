package voto.ado.sainthannaz.votoseguro.aplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rey.material.widget.Button;
import com.rey.material.widget.SnackBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import voto.ado.sainthannaz.votoseguro.R;
import voto.ado.sainthannaz.votoseguro.app.AppController;
import voto.ado.sainthannaz.votoseguro.helper.SQLiteHandler;
import voto.ado.sainthannaz.votoseguro.helper.SessionManager;

/**
 * Created by SaintHannaz on 30/05/2016.
 */
public class ActivityVoting extends AppCompatActivity{
    private static final String TAG = ActivityVoting.class.getSimpleName();
    final Context context = this;
    private RadioGroup radioGroup;
    private String code, bus, category, date, site, classification, type, suggestion, vote, email_vote, userIP;
    private SQLiteHandler db;
    private SessionManager session;
    private CoordinatorLayout coordinatorLayout;
    private SnackBar mSnackBar;
    private TextView codeView, busView, categoryView, dateView, siteView, classificationView, typeView, suggestionView, userName;
    Button btnVote;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voteprocess);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        final View coordinatorLayoutView = findViewById(R.id.coordinatorLayout);

        radioGroup = (RadioGroup) findViewById(R.id.rdVote);
        //radioGroup.check(R.id.rdSR);
        //radioGroup.clearCheck();
        codeView = (TextView) findViewById( R.id.code);
        busView = (TextView) findViewById( R.id.bus);
        categoryView = (TextView) findViewById( R.id.category);
        dateView = (TextView) findViewById( R.id.date);
        siteView = (TextView) findViewById( R.id.site);
        classificationView = (TextView) findViewById( R.id.classification);
        typeView = (TextView) findViewById( R.id.type);
        suggestionView = (TextView) findViewById( R.id.suggestion);
        userName = (TextView) findViewById(R.id.userName);
        btnVote = (Button) findViewById(R.id.btnMyVote);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());
        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        userName.setText(name);
        email_vote = user.get("email");
        userIP = user.get("ip");

        Intent intent=getIntent();
        Bundle extras =intent.getExtras();
        if (extras != null) {//ver si contiene datos
            code=(String)extras.get("erpco");//Codigo ERPCO
            bus= (String) extras.get("bus");//Bus
            category= (String) extras.get("category");//Bus
            date= (String) extras.get("date");//Bus
            site= (String) extras.get("site");//Bus
            classification= (String) extras.get("classification");//Bus
            type= (String) extras.get("type");//Bus
            suggestion= (String) extras.get("suggestion");//Bus

            codeView.setText(getString(R.string.txt_codesession) + " "  + code);
            busView.setText(getString(R.string.txt_bus) + " " + bus);
            categoryView.setText(getString(R.string.txt_grade) + " "  + category);
            dateView.setText(getString(R.string.txt_codedate) + " " + date);
            siteView.setText(getString(R.string.txt_site) + " "  + site);
            classificationView.setText(getString(R.string.txt_class) + " "  + classification);
            typeView.setText(getString(R.string.txt_typealt) + " "  + type);
            suggestionView.setText(getString(R.string.txt_policy) + " "  + suggestion);
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                android.widget.RadioButton rb = (android.widget.RadioButton) group.findViewById(checkedId);

                //rb.getVisibility();
                //rb.setChecked(true);
                if (null != rb && checkedId > -1) {
                    vote = rb.getText().toString();
                    Toast.makeText(ActivityVoting.this, rb.getText(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        // add button listener
        btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_checkvote);
                dialog.setTitle("¡Advertencia!");

                //dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);

                // set the custom dialog components - text, image and button
                TextView titleD = (TextView) dialog.findViewById(R.id.title_txt);
                TextView erpcoD = (TextView) dialog.findViewById(R.id.erpco_txt);
                TextView busD = (TextView) dialog.findViewById(R.id.bus_txt);
                TextView categoryD = (TextView) dialog.findViewById(R.id.category_txt);
                TextView dateD = (TextView) dialog.findViewById(R.id.date_txt);
                TextView siteD = (TextView) dialog.findViewById(R.id.site_txt);
                TextView classificationD = (TextView) dialog.findViewById(R.id.classification_txt);
                TextView typeD = (TextView) dialog.findViewById(R.id.type_txt);
                TextView suggestionD = (TextView) dialog.findViewById(R.id.suggestion_txt);

                titleD.setText("Usted votara " + vote);
                erpcoD.setText("Código: " + code);
                busD.setText("Autobus: " + bus);
                categoryD.setText("Categoria: " + category);
                dateD.setText("Fecha: " + date);
                siteD.setText("Lugar del incidente: " + site);
                classificationD.setText("Clasificación: " + classification);
                typeD.setText("Tipo: " + type);
                suggestionD.setText("Por politica se recomienda votar: " + suggestion);

                //ImageView image = (ImageView) dialog.findViewById(R.id.image);
                //image.setImageResource(R.drawable.ic_action_eye_open);

                Button okButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (vote == null) {
                            Snackbar
                                    .make(coordinatorLayoutView, "Seleccione un voto valido", Snackbar.LENGTH_LONG)
                                    .setAction(R.string.snackbar_action_ok, clickListener)
                                    .show();
                            dialog.dismiss();
                        } else {
                            registerVote(code, email_vote, vote, "votado", userIP);
                            dialog.dismiss();
                            Snackbar
                                    .make(coordinatorLayoutView, "Su voto a sido registrado", Snackbar.LENGTH_LONG)
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
                    }
                });

                Button cancelButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
                // if button is clicked, close the custom dialog
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                //dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.ic_action_eye_open);
            }
        });
    }

    private void registerVote(final String code_vote, final String email_vote,
                              final String vote, final String status_vote, final String userIP) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registrando ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                "http://" + userIP + "/android_login_api/registerVote.php", new Response.Listener<String>() {
                //AppConfig.URL_REGISTER_VOTE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONObject voting = jObj.getJSONObject("voting");
                        String code_vote = voting.getString("code_vote");
                        String email_vote = voting.getString("email_vote");
                        String vote = voting.getString("vote");
                        String status_vote = voting.getString("status_vote");
                        String date_vote = voting.getString("date_vote");

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
                params.put("code_vote", code_vote);
                params.put("email_vote", email_vote);
                params.put("vote", vote);
                params.put("status_vote", status_vote);
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
