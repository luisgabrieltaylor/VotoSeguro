package voto.ado.sainthannaz.votoseguro.aplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rey.material.widget.SnackBar;
import java.util.HashMap;

import voto.ado.sainthannaz.votoseguro.R;
import voto.ado.sainthannaz.votoseguro.helper.SQLiteHandler;
import voto.ado.sainthannaz.votoseguro.helper.SessionManager;


public class ActivityMainMenu extends AppCompatActivity {

    private Button mMenu1;
    private Button mMenu2;
    private Button mMenu3;
    private Button mMenu4;
    private TextView UserName, serverIP;
    private TextView txtName;
    private String userLevel, userIP;
    SnackBar mSnackBar;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);



        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {

            }
        };
        final View coordinatorLayoutView = findViewById(R.id.coordinatorLayout);

        Snackbar
                .make(coordinatorLayoutView, R.string.snack_main_menu, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action_ok, clickListener)
                .show();

        mMenu1 = (Button) findViewById(R.id.Button1);
        mMenu2 = (Button) findViewById(R.id.Button2);
        mMenu3 = (Button) findViewById(R.id.Button3);
        mMenu4 = (Button) findViewById(R.id.Button4);
        UserName = (TextView) findViewById(R.id.userName);
        //serverIP = (TextView) findViewById(R.id.serverIP);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        System.out.println(name);
        userLevel = user.get("level");
        System.out.println(userLevel);
        userIP = user.get("ip");
        System.out.println(userIP);
        UserName.setText(name);
        //serverIP.setText(userIP);

        //Se obtiene la MAC del dispositivo en que se opera
        WifiManager wimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String MACaddress = wimanager.getConnectionInfo().getMacAddress();
        TextView mymacAdress = (TextView) findViewById(R.id.macAdress);
        mymacAdress.setText("MAC: " + MACaddress);

        if (userLevel.equals("Administrador")) {

            Menu1();
            Menu2();
            Menu3();
            Menu4();

        } else {
            mMenu1.setVisibility(View.GONE);
            mMenu2.setVisibility(View.GONE);
            Menu3();
            Menu4();
        }

    }

    private void Menu1() {
        mMenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityMainMenu.this, ActivityGeneral.class);
                startActivity(i);

            }
        });
    }

    private void Menu2() {
        mMenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityMainMenu.this, ActivityReports.class);
                startActivity(i);
            }
        });
    }

    private void Menu3() {
        mMenu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityMainMenu.this, ActivityVote.class);
                startActivity(i);


            }
        });
    }

    private void Menu4() {
        mMenu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent i = new Intent(ActivityMainMenu.this, ActivityReport.class);
                // startActivity(i);
                dialogoSalir();

            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(
            Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about_app) {
            about();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed()
    {
        //Toast.makeText(getApplicationContext(), "",
        //        Toast.LENGTH_SHORT).show();
        dialogoSalir();
        return;
    }

    private void dialogoSalir()
    {
        new AlertDialog.Builder(this)
                .setTitle(R.string.str_title_exit)
                .setIcon(R.drawable.ic_action_exit)
                .setMessage(R.string.str_exit_message)
                .setNegativeButton(R.string.str_no,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialoginterface, int i)
                            {

                            }
                        }
                )
                .setPositiveButton(R.string.str_si,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialoginterface, int i)
                            {

                                logoutUser();
                            }
                        }
                )
                .show();
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(ActivityMainMenu.this, ActivityLogin.class);
        startActivity(intent);
        finish();
    }

    private void about() {

        // Launching the login activity
        Intent intent = new Intent(ActivityMainMenu.this, ActivityAbout.class);
        startActivity(intent);
    }
}
