package com.x7.steambadger.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.x7.steambadger.database.model.Badge;
import com.x7.steambadger.view.BadgeView;

import java.util.ArrayList;
import java.util.List;

public class BadgeAdapter extends BaseAdapter {

    private Context context;
    private List<Badge> adapter;

    public BadgeAdapter(Context context) {
        this.context = context;
        this.adapter = new ArrayList<>();
    }

    public void add(Badge badge) {
        adapter.add(badge);
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

    public void clear() {
        adapter.clear();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Badge badge = adapter.get(position);

        BadgeView view = (BadgeView) convertView;

        if (convertView == null) {
            view = new BadgeView(context, badge);
        }

        view.setBadge(badge);
        view.refreshBadgeData();

        return view;
    }

}