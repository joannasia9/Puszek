package com.puszek.jm.puszek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {
    public static final int REGISTER_USER_RESULT = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toasty.Config.getInstance()
                .setErrorColor(getColor(R.color.errorColor))
                .setInfoColor(getColor(R.color.infoColor))
                .setSuccessColor(getColor(R.color.buttonGreenCenter))
                .setWarningColor(getColor(R.color.warningColor))
                .setTextColor(getColor(R.color.backgroundLight))
                .tintIcon(true)
                .setTextSize(16)
                .apply();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REGISTER_USER_RESULT) {
            if(resultCode == Activity.RESULT_OK){
                Boolean result = data.getBooleanExtra("result", false);
                if(result)
                    Toasty.success(this, getString(R.string.registration_suceed), Toast.LENGTH_SHORT, true).show();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void loginUser(View view) {
        //startActivity(new Intent(getApplicationContext(),LoginUserActivity.class));
        startActivity(new Intent(getApplicationContext(),MainMenuActivity.class));
    }

    public void goToRegistrationPanel(View view) {
        startActivityForResult(new Intent(getApplicationContext(),RegisterUserActivity.class), REGISTER_USER_RESULT);
    }
}
