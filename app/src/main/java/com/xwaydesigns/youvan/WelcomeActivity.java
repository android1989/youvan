package com.xwaydesigns.youvan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.xwaydesigns.youvan.ExtraClasses.Constants;
import com.xwaydesigns.youvan.ExtraClasses.SessionManager;

import java.util.HashMap;

public class WelcomeActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000;
    private HashMap<String,String> user_data;
    private ImageView imageView;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        imageView = findViewById(R.id.welcome_logo);
        Animation fadingInAnimation = AnimationUtils.loadAnimation(WelcomeActivity.this,R.anim.fade_in);
        fadingInAnimation.setDuration(SPLASH_DURATION);
        imageView.startAnimation(fadingInAnimation);
///////////////////////////////////
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {

                SessionManager manager = new SessionManager(WelcomeActivity.this);
                if (!manager.IsRegistered() && !manager.IsLoggedIn())
                {
                    Intent start = new Intent(WelcomeActivity.this,LoginActivity.class);
                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(start);
                    finish();
                }

                else if (manager.IsRegistered() && !manager.IsLoggedIn())
                {
                    Intent start = new Intent(WelcomeActivity.this,LoginActivity.class);
                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(start);
                    finish();
                }


                else if (manager.IsRegistered() && manager.IsLoggedIn())
                {
                    Intent start = new Intent(WelcomeActivity.this,MainActivity.class);
                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(start);
                    finish();
                }

                else
                {
                    //do nothing
                }

            }
        },SPLASH_DURATION);

 ////////////////////////////////////////////////////





    }
}