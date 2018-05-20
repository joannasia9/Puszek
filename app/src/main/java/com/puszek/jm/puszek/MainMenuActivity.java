package com.puszek.jm.puszek;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.puszek.jm.puszek.adapters.MainMenuAdapter;
import com.puszek.jm.puszek.utils.PermissionResultCallback;
import com.puszek.jm.puszek.utils.PermissionUtils;

import java.util.ArrayList;

public class MainMenuActivity extends MyBaseActivity implements PermissionResultCallback{
    ListView listView;
    MainMenuAdapter mainMenuAdapter;
    PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        requestPermissions();

        listView = findViewById(R.id.menuListView);
        mainMenuAdapter = new MainMenuAdapter(this);

        listView.setAdapter(mainMenuAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(getApplicationContext(), BarcodeReadingActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), ObjectVerificationActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                }
            }
        });

    }

    private void requestPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.INTERNET);

        permissionUtils = new PermissionUtils(this, this);
        permissionUtils.checkPermission(permissions, PermissionUtils.ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION", "GRANTED");
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY", "GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION", "DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION", "NEVER ASK AGAIN");

    }
}
