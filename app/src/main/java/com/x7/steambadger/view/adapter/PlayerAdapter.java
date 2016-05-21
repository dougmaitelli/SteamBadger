package com.x7.steambadger.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.x7.steambadger.R;
import com.x7.steambadger.database.model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends BaseAdapter {

    private Context context;
    private List<Player> adapter;

    public PlayerAdapter(Context context) {
        this.context = context;
        this.adapter = new ArrayList<>();
    }

    public void add(Player player) {
        adapter.add(player);
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
        Player player = adapter.get(position);

        View view = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.player_row, parent, false);
        }

        ImageView playerAvatar = (ImageView) view.findViewById(R.id.player_avatar);
        TextView playerName = (TextView) view.findViewById(R.id.player_name);

        Picasso.with(context).load(player.getAvatarUrl()).into(playerAvatar);
        playerName.setText(player.getName());

        return view;
    }

}