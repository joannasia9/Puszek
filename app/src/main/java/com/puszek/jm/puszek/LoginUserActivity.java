package com.puszek.jm.puszek;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginUserActivity extends AppCompatActivity {
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

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void signInUser(View view) {
    }
}
