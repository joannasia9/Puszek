package com.puszek.jm.puszek;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import com.puszek.jm.puszek.utils.PermissionManager;

public class BarcodeReadingActivity extends MyBaseActivity {
    TextView verificationOptionTitle;
    Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reading);

        verificationOptionTitle = findViewById(R.id.verificationOptionTitle);

        String optionTitle = getString(R.string.scan) + "\n" + getString(R.string.barcode);
        verificationOptionTitle.setText(optionTitle);

        if(PermissionManager.hasCamPermission(this))
            replaceFragmentContent();
    }

    private void replaceFragmentContent(){
        fragment = new BarcodeReadingFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.readingContent, fragment).commit();
    }

}
