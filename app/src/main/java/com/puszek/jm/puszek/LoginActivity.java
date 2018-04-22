package com.puszek.jm.puszek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {
    public static final int REGISTER_USER_RESULT = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    public void goToLoginPanel(View view) {
        startActivity(new Intent(this,LoginUserActivity.class));
    }


    public void goToRegisterPanel(View view) {
        startActivityForResult(new Intent(this,RegisterUserActivity.class), REGISTER_USER_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REGISTER_USER_RESULT) {
            if(resultCode == Activity.RESULT_OK){
                Boolean result = data.getBooleanExtra("result", false);
                if(result) Toast.makeText(this,getString(R.string.registration_suceed),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
