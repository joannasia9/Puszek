package com.puszek.jm.puszek;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.puszek.jm.puszek.utils.PermissionManager;

public class ObjectVerificationActivity extends MyBaseActivity {
    Switch mSwitch;
    TextView verificationOptionTitle;
    CheckBox checkBox;
    Fragment fragment;

    CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                scanBarcodeActionPerform();
                Toast.makeText(ObjectVerificationActivity.this,
                        "Switch On", Toast.LENGTH_SHORT).show();
                mSwitch.setThumbResource(R.drawable.barcode);
            } else {
                Toast.makeText(ObjectVerificationActivity.this, "Switch Off", Toast.LENGTH_SHORT).show();
                scanProductActionPerform();
                mSwitch.setThumbResource(R.drawable.box);
            }
        }
    };

    CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                //flashLightOn();
            } else {
                // flashLightOff();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_verification);


        mSwitch = findViewById(R.id.modeSwitch);
        verificationOptionTitle = findViewById(R.id.verificationOptionTitle);

        if(PermissionManager.hasCamPermission(this))
        scanProductActionPerform();

        mSwitch.setOnCheckedChangeListener(switchListener);

        checkBox = findViewById(R.id.useFlashCheckbox);

        if(isFlashAvailable()) checkBox.setOnCheckedChangeListener(checkBoxListener);
        else {
            checkBox.setAlpha((float) 0.2);
            checkBox.setClickable(false);
        }
    }

    public void scanBarcodeActionPerform() {
        String optionTitle = getString(R.string.scan) + "\n" + getString(R.string.barcode);
        verificationOptionTitle.setText(optionTitle);
        fragment = new BarcodeReadingFragment();
        replaceFragmentContent();
    }

    public void scanProductActionPerform(){
        String optionTitle = getString(R.string.scan) + "\n" + getString(R.string.packageStr);
        verificationOptionTitle.setText(optionTitle);
        fragment = new BoxReadingFragment();
        replaceFragmentContent();
    }

    private void replaceFragmentContent(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.readingContent, fragment).commit();
    }

    private boolean isFlashAvailable(){
        boolean isAvailable = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if(!isAvailable){
            Toast.makeText(this, R.string.flash_unavailable,Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }

    CameraManager camManager;
    String cameraId;
    public void flashLightOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            camManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
            try {
                assert camManager != null;
                cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, true);   //Turn ON flash
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void flashLightOff() {
        try {
            camManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



}
