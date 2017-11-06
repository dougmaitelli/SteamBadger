package com.x7.steambadger.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.x7.steambadger.util.AppMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends BaseAdapter {

    private Context context;
    private List<AppMenu> adapter;

    public MenuAdapter(Context context) {
        super();
        this.context = context;
        this.adapter = new ArrayList<AppMenu>();
    }
    
    public void add(AppMenu item) {
		adapter.add(item);
	}
    
    public long getItemId(int position) {
        return position;
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    
    public Object getItem(int position) {
        return adapter.get(position);
    }

    public int getCount() {
        return adapter.size();
    }

    public int getViewTypeCount() {
        return 1;
    }

    public int getItemViewType(int position) {
        return 1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	AppMenu item = adapter.get(position);

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }

        view.setTag(item.name());

        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setText(item.getDescription());
            
        return view;
    }
}