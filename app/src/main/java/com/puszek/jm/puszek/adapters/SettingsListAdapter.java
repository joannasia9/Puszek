package com.puszek.jm.puszek.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.puszek.jm.puszek.R;
import com.puszek.jm.puszek.viewholders.SettingsItemViewHolder;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SettingsListAdapter extends BaseAdapter {
    private String[] settingsTitles;
    private ArrayList<String> settingsItemsValues;
    private Context context;

    public SettingsListAdapter(Context c, ArrayList<String> settingsItemsValues, String[] settingsTitles){
        this.context = c;
        this.settingsTitles = settingsTitles;
        this.settingsItemsValues = settingsItemsValues;
    }

    @Override
    public int getCount() {
        return settingsTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SettingsItemViewHolder settingsItemViewHolder;


        if (convertView == null) {
            settingsItemViewHolder = new SettingsItemViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.listview_settings_item, parent, false);

            settingsItemViewHolder.settingsItemTitle = convertView.findViewById(R.id.settingsItemTitle);
            settingsItemViewHolder.settingsItemValue = convertView.findViewById(R.id.settingsItemValue);
            settingsItemViewHolder.settingsItemLayout = convertView.findViewById(R.id.settingsItemLayout);
            convertView.setTag(settingsItemViewHolder);
        } else {
            settingsItemViewHolder = (SettingsItemViewHolder) convertView.getTag();
        }

        settingsItemViewHolder.settingsItemTitle.setText(settingsTitles[position]);
        settingsItemViewHolder.settingsItemValue.setText(settingsItemsValues.get(position));

        if(position%2==0){
            settingsItemViewHolder.settingsItemLayout.setBackgroundColor(ContextCompat.getColor(context,R.color.buttonGreenEnd));
        }

        return  convertView;
    }

    public void updateAdapter(ArrayList<String> settingsItemsValues){
        this.settingsItemsValues = settingsItemsValues;
        this.notifyDataSetChanged();
    }
}
