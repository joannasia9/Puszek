package com.puszek.jm.puszek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.puszek.jm.puszek.helpers.FieldsValidator;
import com.puszek.jm.puszek.models.APIClient;
import com.puszek.jm.puszek.models.ApiInterface;
import com.puszek.jm.puszek.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        User user = new User(login.getText().toString().trim(),email.getText().toString(),password.getText().toString(),
                district.getText().toString(), street.getText().toString(),hNumber.getText().toString());

        final ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
        final Call<User> registerUserCall = apiInterface.createUser(user);

        registerUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if(response.code()!= 201){
                    login.setError(getString(R.string.user_exists));
                    login.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.showSoftInput(login, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",true);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }

                Log.e("SERVER RESPONSE", response.toString());

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                call.cancel();
            }
        });

    }

}
