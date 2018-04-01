package com.puszek.jm.puszek;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;

import com.puszek.jm.puszek.helpers.FieldsValidator;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterUserActivity extends AppCompatActivity {
    EditText login;
    EditText password;
    EditText repeatedPassword;
    EditText street;
    EditText city;
    EditText hNumber;
    Button registerUserButton;
    FieldsValidator validator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        validator = new FieldsValidator(this);
        login = findViewById(R.id.regLoginEt);
        password = findViewById(R.id.regPasswordEt);
        repeatedPassword = findViewById(R.id.regRepPasswordEt);
        street = findViewById(R.id.regStreetEt);
        city = findViewById(R.id.city);
        hNumber = findViewById(R.id.houseNumber);

        registerUserButton = findViewById(R.id.regUserButton);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void registerUser(View view) {
        if(validator.isValidField(login)&&validator.isValidPassword(password,repeatedPassword)
                && validator.isValidField(city) &&validator.isValidField(street)&&validator.isValidHouseNumber(hNumber)){
            //register user at database
            System.out.println("HURRAAAA");
        }
    }
}
