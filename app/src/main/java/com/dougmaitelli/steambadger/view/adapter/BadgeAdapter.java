package com.dougmaitelli.steambadger.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dougmaitelli.steambadger.database.model.Badge;
import com.dougmaitelli.steambadger.database.model.Player;
import com.dougmaitelli.steambadger.view.BadgeView;

import java.util.ArrayList;
import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ViewHolder> {

    private Context context;
    private Player player;
    private List<Badge> items;
    private OnItemClickListener onItemClickListener;

    public BadgeAdapter(Context context, Player player) {
        this.context = context;
        this.player = player;
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
        View v = new BadgeView(context, player, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(Badge item) {
        items.add(item);
        int position = items.indexOf(item);
        notifyItemInserted(position);
    }

    public void add(Badge item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Badge item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public BadgeView badgeView;

        public ViewHolder(View itemView) {
            super(itemView);
            badgeView = (BadgeView) itemView;
        }

        public void bind(final Badge item, final OnItemClickListener listener) {
            badgeView.setBadge(item);
        }
    }

}