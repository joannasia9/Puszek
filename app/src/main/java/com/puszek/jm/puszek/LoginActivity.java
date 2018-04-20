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

    }

    public void goToLoginPanel(View view) {
        startActivity(new Intent(this,LoginUserActivity.class));
    }


    public void goToRegisterPanel(View view) {
        startActivity(new Intent(this,RegisterUserActivity.class));
    }
}
