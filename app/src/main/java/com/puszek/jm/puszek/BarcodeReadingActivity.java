package com.puszek.jm.puszek;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.puszek.jm.puszek.helpers.BarcodeGraphic;
import com.puszek.jm.puszek.helpers.BarcodeGraphicTracker;
import com.puszek.jm.puszek.helpers.BarcodeTrackerFactory;
import com.puszek.jm.puszek.models.APIClient;
import com.puszek.jm.puszek.models.ApiInterface;
import com.puszek.jm.puszek.models.RequestedBarcodeData;
import com.puszek.jm.puszek.ui.camera.CameraSourcePreview;
import com.puszek.jm.puszek.ui.camera.GraphicOverlay;
import com.puszek.jm.puszek.utils.PermissionManager;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarcodeReadingActivity extends MyBaseActivity {
    Switch mSwitch;
    TextView verificationOptionTitle;
    Fragment fragment;

    CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                //flashLightOn();
                mSwitch.setAlpha(1);
            } else {
                //flashLightOff();
                mSwitch.setAlpha((float) 0.5);
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reading);

        mSwitch = findViewById(R.id.modeSwitch);
        mSwitch.setThumbResource(R.drawable.flash);
        verificationOptionTitle = findViewById(R.id.verificationOptionTitle);

        if (!isFlashAvailable()) mSwitch.setClickable(false);

        String optionTitle = getString(R.string.scan) + "\n" + getString(R.string.barcode);
        verificationOptionTitle.setText(optionTitle);

        if(PermissionManager.hasCamPermission(this))
            replaceFragmentContent();

        mSwitch.setOnCheckedChangeListener(switchListener);

    }

    private void replaceFragmentContent(){
        fragment = new BarcodeReadingFragment();
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
