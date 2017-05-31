package voto.ado.sainthannaz.votoseguro.aplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import voto.ado.sainthannaz.votoseguro.R;

public class SplashScreen extends Activity implements Animation.AnimationListener {
    protected boolean _active = true;
    protected int _splashTime = 1500;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window. FEATURE_NO_TITLE); // Permite la visualizacion de la aplicacion sin ventana de titulo
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,// Permite la visualizacion de la aplicacion sin ventana de titulo
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  // Permite la visualizacion de la aplicacion sin ventana de titulo
        setContentView(R.layout.activity_splash_screen);

        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setVisibility(ImageView.VISIBLE);

// load the animation
        Animation animMoveDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.movedown);
        Animation animFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
// set animation listener
        //animMoveDown.setAnimationListener(this);
        //logo.startAnimation(animMoveDown);
        //animFade.setAnimationListener(this);


        // thread for displaying the SplashScreen
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while(_active && (waited < _splashTime)) {
                        sleep(200);
                        if(_active) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                    startActivity(new Intent(SplashScreen.this, ActivityLogin.class));
                    onStop();
                }
            }
        };
        splashTread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _active = false;
        }
        return true;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}