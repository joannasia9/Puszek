package com.puszek.jm.puszek;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.puszek.jm.puszek.asynctasks.SendUserDetails;
import com.puszek.jm.puszek.helpers.FieldsValidator;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterUserActivity extends MyBaseActivity {
    EditText login;
    EditText password;
    EditText repeatedPassword;
    EditText street;
    EditText district;
    EditText hNumber;
    EditText email;
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
        email = findViewById(R.id.emailEt);

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
        email.setOnClickListener(animationListener);

    }

    public void registerUser(View view) {
        if(validator.isValidField(login)&&validator.isValidPassword(password,repeatedPassword)
                && validator.isValidField(district) &&validator.isValidField(street)&&validator.isValidHouseNumber(hNumber)){
            //register user at database
            clearAllAnimations();
            sendUserDataToServer();
            System.out.println("Registered");
        }
    }

    public void setAnimation(View v){
        int id = v.getId();
        System.out.println("IDD: "+id);

        clearAllAnimations();

        Animation iconAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.dots_anim);
        v.startAnimation(iconAnimation);
    }

    private void clearAllAnimations(){
        login.clearAnimation();
        password.clearAnimation();
        email.clearAnimation();
        repeatedPassword.clearAnimation();
        street.clearAnimation();
        hNumber.clearAnimation();
        district.clearAnimation();

    }

    private void sendUserDataToServer(){
        JSONObject postData = new JSONObject();
        try {
            postData.put("login", login.getText().toString().trim());
            postData.put("email", email.getText().toString());
            postData.put("password", password.getText().toString());
            postData.put("district", district.getText().toString());
            postData.put("street", street.getText().toString());
            postData.put("number", hNumber.getText().toString());
            SendUserDetails sendUserDetails = new SendUserDetails();
            sendUserDetails.execute("https://afternoon-ridge-77405.herokuapp.com/api/v1/users/", postData.toString());
            } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
