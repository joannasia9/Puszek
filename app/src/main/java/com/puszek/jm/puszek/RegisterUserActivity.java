package com.puszek.jm.puszek;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterUserActivity extends AppCompatActivity {
    EditText login;
    EditText password;
    EditText repeatedPassword;
    EditText street;
    Button registerUserButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        login = findViewById(R.id.regLoginEt);
        password = findViewById(R.id.regPasswordEt);
        repeatedPassword = findViewById(R.id.regRepPasswordEt);
        street = findViewById(R.id.regStreetEt);

        registerUserButton = findViewById(R.id.regUserButton);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void registerUser(View view) {
    }
}
