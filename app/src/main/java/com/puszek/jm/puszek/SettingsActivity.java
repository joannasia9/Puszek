package com.puszek.jm.puszek;

import android.graphics.PorterDuff;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import com.puszek.jm.puszek.adapters.SettingsListAdapter;

import java.util.ArrayList;

public class SettingsActivity extends MyBaseActivity{
    ListView settingsListView;
    SettingsListAdapter settingsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsListView = findViewById(R.id.settingsListView);
        settingsListAdapter = new SettingsListAdapter(this);

        settingsListView.setAdapter(settingsListAdapter);
       final Animation itemAnimation = AnimationUtils.loadAnimation(this, R.anim.settings_item_animation);

        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.startAnimation(itemAnimation);
            }
        });

    }
}
