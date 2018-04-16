package com.puszek.jm.puszek;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends MyBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/ArchitectsDaughter.ttf");

        TextView textView = findViewById(R.id.textView);
        textView.setTypeface(typeface);

        Button loginButton = findViewById(R.id.signInButtonMain);
        loginButton.setTypeface(typeface);

        Button registerButton = findViewById(R.id.registerButtonMain);
        registerButton.setTypeface(typeface);

    }

    public void goToLoginPanel(View view) {
        startActivity(new Intent(this,LoginUserActivity.class));
    }


    public void goToRegisterPanel(View view) {
        startActivity(new Intent(this,RegisterUserActivity.class));
    }
}
