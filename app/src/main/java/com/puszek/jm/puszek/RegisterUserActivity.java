package com.puszek.jm.puszek;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.puszek.jm.puszek.helpers.FieldsValidator;
import com.puszek.jm.puszek.models.APIClient;
import com.puszek.jm.puszek.models.ApiInterface;
import com.puszek.jm.puszek.models.RegisteredUser;
import com.puszek.jm.puszek.models.User;

import es.dmoral.toasty.Toasty;
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

        login.requestFocus();

    }

    public void registerUser(View view) {
        if(validator.isValidField(login)&&validator.isValidPassword(password,repeatedPassword)
                && validator.isValidField(district) &&validator.isValidField(street)&&validator.isValidHouseNumber(hNumber)){
            clearAllAnimations();
            sendUserDataToServer(login, email, password, district, street, hNumber);
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

    private void sendUserDataToServer(final EditText login, EditText email, EditText password, EditText district, EditText street, EditText hNumber){
        User user = new User(login.getText().toString().trim(),email.getText().toString(),password.getText().toString(),
                district.getText().toString(), street.getText().toString(),hNumber.getText().toString());

        final ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);

        final Call<RegisteredUser> registerUserCall = apiInterface.createUser(user);

        registerUserCall.enqueue(new Callback<RegisteredUser>() {
            @Override
            public void onResponse(Call<RegisteredUser> call, Response<RegisteredUser> response) {
                if(response.code() >= 300){
                    Toasty.error(getApplicationContext(), getString(R.string.user_exists), Toast.LENGTH_SHORT, true).show();
                    login.setError(getString(R.string.user_exists));
                    login.requestFocus();
                    Log.e("REGISTERED USER ID", "onResponse: " + response.body().getId());
                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",true);

                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }

                Log.e("SERVER RESPONSE", response.toString());
            }

            @Override
            public void onFailure(Call<RegisteredUser> call, Throwable t) {
                call.cancel();
            }
        });

    }

}
