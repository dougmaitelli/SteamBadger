package com.dougmaitelli.steambadger.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.dougmaitelli.steambadger.R;
import com.dougmaitelli.steambadger.database.model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    private Context context;
    private List<Player> items;
    private OnItemClickListener onItemClickListener;

    public PlayerAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    public interface OnItemClickListener {
        void onItemClick(Player item);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position), onItemClickListener);
    }

    @Override public int getItemCount() {
        return items.size();
    }

    public void add(Player item) {
        items.add(item);
        int position = items.indexOf(item);
        notifyItemInserted(position);
    }

    public void add(Player item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Player item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView playerAvatar;
        public TextView playerName;

        public ViewHolder(View itemView) {
            super(itemView);
            playerAvatar = (ImageView) itemView.findViewById(R.id.player_avatar);
            playerName = (TextView) itemView.findViewById(R.id.player_name);
        }

        public void bind(final Player item, final OnItemClickListener listener) {
            Picasso.with(context).load(item.getAvatarUrl()).into(playerAvatar);
            playerName.setText(item.getName());
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }

            });

        }
    }

}