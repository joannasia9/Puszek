package com.puszek.jm.puszek;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;

import com.puszek.jm.puszek.adapters.MainMenuAdapter;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainMenuActivity extends MyBaseActivity {
    ListView listView;
    MainMenuAdapter mainMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        listView = findViewById(R.id.menuListView);
        mainMenuAdapter = new MainMenuAdapter(this);

        listView.setAdapter(mainMenuAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(getApplicationContext(), ObjectVeryficationActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), ObjectAdditionActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                }
            }
        });

    }


}
