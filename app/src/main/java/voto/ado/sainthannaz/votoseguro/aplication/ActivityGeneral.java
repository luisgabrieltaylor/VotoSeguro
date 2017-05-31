package voto.ado.sainthannaz.votoseguro.aplication;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import voto.ado.sainthannaz.votoseguro.R;
import voto.ado.sainthannaz.votoseguro.helper.SQLiteHandler;
import voto.ado.sainthannaz.votoseguro.helper.SessionManager;


public class ActivityGeneral extends AppCompatActivity {
    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TextView UserName;
    private TextView UserEmail;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        ContentFragment_GeneralMain fragment = new ContentFragment_GeneralMain();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        UserName = (TextView) findViewById(R.id.username);
        UserEmail = (TextView) findViewById(R.id.email);

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        //UserName.setText(name);
        //UserEmail.setText(email);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View hView =  navigationView.getHeaderView(0);

        TextView nav_user = (TextView)hView.findViewById(R.id.username);
        nav_user.setText(name);

        TextView nav_email = (TextView)hView.findViewById(R.id.email);
        nav_email.setText(email);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){


                    //Replacing the main content with ContentFragmentMain Which is our Inbox View;
                    case R.id.main:
                        ContentFragment_GeneralMain fragmentMain = new ContentFragment_GeneralMain();
                        android.support.v4.app.FragmentTransaction fragmentTransactionMain = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionMain.replace(R.id.frame,fragmentMain);
                        fragmentTransactionMain.commit();
                        return true;
                    // For rest of the options we just show a toast on click
                    case R.id.users:
                        ContentFragment_GeneralUsers fragmentUsers = new ContentFragment_GeneralUsers();
                        android.support.v4.app.FragmentTransaction fragmentTransactionUsers = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionUsers.replace(R.id.frame,fragmentUsers);
                        fragmentTransactionUsers.commit();
                        return true;
                    case R.id.devices:
                        ContentFragment_GeneralDevices fragmentDevices = new ContentFragment_GeneralDevices();
                        android.support.v4.app.FragmentTransaction fragmentTransactionDevices = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionDevices.replace(R.id.frame,fragmentDevices);
                        fragmentTransactionDevices.commit();
                        return true;

                    case R.id.codes:
                        ContentFragment_GeneralCodes fragmentCodes = new ContentFragment_GeneralCodes();
                        android.support.v4.app.FragmentTransaction fragmentTransactionCodes = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionCodes.replace(R.id.frame,fragmentCodes);
                        fragmentTransactionCodes.commit();
                        return true;

                    case R.id.logout:
                        finish();
                        return true;
                    default:
                        fragmentMain = new ContentFragment_GeneralMain();
                        //Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


    }


}
