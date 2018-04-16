package com.puszek.jm.puszek;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginUserActivity extends MyBaseActivity {
    ImageView imageView;
    Button signInButton;
    TextView login;
    TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        imageView = findViewById(R.id.animationImageView);
        signInButton = findViewById(R.id.signInButtonMain);
        login = findViewById(R.id.loginEditText);
        password = findViewById(R.id.passwordEditText);

        Animation iconAnimation = AnimationUtils.loadAnimation(this, R.anim.login_icon_anim);
        imageView.startAnimation(iconAnimation);

    }

    public void signInUser(View view) {
       startActivity(new Intent(this, MainMenuActivity.class));
    }
}
