package com.puszek.jm.puszek;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ObjectVerificationActivity extends MyBaseActivity {
    Switch mSwitch;
    TextView verificationOptionTitle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_verification);

        mSwitch = findViewById(R.id.modeSwitch);
        verificationOptionTitle = findViewById(R.id.verificationOptionTitle);
        scanProductActionPerform();

        mSwitch.setOnCheckedChangeListener(switchListener);


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

}
