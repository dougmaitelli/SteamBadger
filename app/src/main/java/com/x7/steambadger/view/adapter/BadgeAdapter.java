package com.x7.steambadger.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.x7.steambadger.database.model.Badge;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.view.BadgeView;

import java.util.ArrayList;
import java.util.List;

public class BadgeAdapter extends BaseAdapter {

    private Context context;
    private Player player;
    private List<Badge> adapter;

    public BadgeAdapter(Context context, Player player) {
        this.context = context;
        this.player = player;
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
            view = new BadgeView(context, player, badge);
        }

        view.setBadge(badge);
        view.refreshBadgeData();

        return view;
    }

}