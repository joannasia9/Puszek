package com.puszek.jm.puszek;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;

import com.puszek.jm.puszek.helpers.FieldsValidator;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterUserActivity extends AppCompatActivity {
    EditText login;
    EditText password;
    EditText repeatedPassword;
    EditText street;
    EditText district;
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
        district = findViewById(R.id.city);
        hNumber = findViewById(R.id.houseNumber);

        registerUserButton = findViewById(R.id.regUserButton);

        View.OnClickListener animationListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnimation(v);
            }
        };
        login.setOnClickListener(animationListener);
        password.setOnClickListener(animationListener);
        repeatedPassword.setOnClickListener(animationListener);
        street.setOnClickListener(animationListener);
        district.setOnClickListener(animationListener);
        hNumber.setOnClickListener(animationListener);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void registerUser(View view) {
        if(validator.isValidField(login)&&validator.isValidPassword(password,repeatedPassword)
                && validator.isValidField(district) &&validator.isValidField(street)&&validator.isValidHouseNumber(hNumber)){
            //register user at database
            System.out.println("HURRAAAA");
        }
    }

    public void setAnimation(View v){
        int id = v.getId();
        System.out.println("IDD: "+id);

        login.clearAnimation();
        password.clearAnimation();
        repeatedPassword.clearAnimation();
        street.clearAnimation();
        hNumber.clearAnimation();
        district.clearAnimation();

        Animation iconAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.dots_anim);
        v.startAnimation(iconAnimation);
    }


}
