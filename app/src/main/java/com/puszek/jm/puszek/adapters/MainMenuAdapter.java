package com.puszek.jm.puszek.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.puszek.jm.puszek.viewholders.MenuItemViewHolder;
import com.puszek.jm.puszek.R;

public class MainMenuAdapter extends BaseAdapter {
    private Context context;
    private Integer[] menuItemImages = {R.drawable.verify_item, R.drawable.settings_item};
    private String[] menuItemsTitles;

    public MainMenuAdapter(Context c){
        this.context = c;
        this.menuItemsTitles= new String[]{c.getString(R.string.check), c.getString(R.string.settings)};
    }

    @Override
    public int getCount() {
        return menuItemsTitles.length;
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
        MenuItemViewHolder menuItemViewHolder;


        if (convertView == null) {
            menuItemViewHolder = new MenuItemViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.listview_menu_item, parent, false);

            menuItemViewHolder.menuItemTitle = convertView.findViewById(R.id.menuItemTitle);
            menuItemViewHolder.menuItemImageView = convertView.findViewById(R.id.menuItemImageView);
            convertView.setTag(menuItemViewHolder);
        } else {
            menuItemViewHolder = (MenuItemViewHolder) convertView.getTag();
        }

        menuItemViewHolder.menuItemImageView.setImageResource(menuItemImages[position]);
        menuItemViewHolder.menuItemTitle.setText(menuItemsTitles[position]);

        return  convertView;
    }

}
