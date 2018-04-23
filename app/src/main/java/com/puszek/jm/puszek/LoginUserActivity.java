package com.puszek.jm.puszek;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.puszek.jm.puszek.helpers.FieldsValidator;
import com.puszek.jm.puszek.models.APIClient;
import com.puszek.jm.puszek.models.ApiInterface;
import com.puszek.jm.puszek.models.AuthenticationRequest;
import com.puszek.jm.puszek.models.AuthenticationRequestResult;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginUserActivity extends MyBaseActivity {
    ImageView imageView;
    Button signInButton;
    EditText login;
    EditText password;
    Boolean permissionsGranted = false;
    SharedPreferences puszekPrefs;
    Context mainContext = this;

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

        permissionsGranted = isWriteStoragePermissionGranted();

    }

    public void signInUser(View view) {
        FieldsValidator validator = new FieldsValidator(this);

        if(validator.isValidField(login)&&validator.isValidField(password)){

            AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                    login.getText().toString().trim(), password.getText().toString());

            final ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
            final Call<AuthenticationRequestResult> authenticateUser = apiInterface.authenticate(authenticationRequest);

            authenticateUser.enqueue(new Callback<AuthenticationRequestResult>() {
                @Override
                public void onResponse(Call<AuthenticationRequestResult> call, Response<AuthenticationRequestResult> response) {

                    if(response.code()>199 && response.code()<300){
                    puszekPrefs = mainContext.getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = puszekPrefs.edit();
                    if(response.body() != null) {
                        editor.putString(mainContext.getString(R.string.access_token_id), response.body().getAccessToken());
                        editor.putString(mainContext.getString(R.string.refresh_token_id), response.body().getRefreshToken());
                    }
                    editor.apply();
                        startActivity(new Intent(mainContext, MainMenuActivity.class));
                    } else {
                        login.setText("");
                        password.setText("");
                        login.requestFocus();
                        Toasty.error(mainContext, getString(R.string.wring_login_password), Toast.LENGTH_LONG,true).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthenticationRequestResult> call, Throwable t) {
                        call.cancel();
                }
            });
        }

    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("PERMISSIONS","Permission is granted2");
                return true;
            } else {

                Log.v("PERMISSIONS","Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("PERMISSIONS","Permission is granted2");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("PERMISSIONS","Permission: "+permissions[0]+ "was "+grantResults[0]);
        } else isWriteStoragePermissionGranted();
    }

}
