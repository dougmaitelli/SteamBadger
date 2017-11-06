package com.dougmaitelli.steambadger.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.dougmaitelli.steambadger.MainActivity;
import com.dougmaitelli.steambadger.R;
import com.dougmaitelli.steambadger.database.model.Player;
import com.dougmaitelli.steambadger.database.model.PlayerBadge;
import com.dougmaitelli.steambadger.util.LoaderTask;
import com.dougmaitelli.steambadger.util.Util;
import com.dougmaitelli.steambadger.view.ProfileHeaderView;
import com.dougmaitelli.steambadger.view.adapter.BadgeAdapter;
import com.dougmaitelli.steambadger.ws.Ws;

public class BadgesFragment extends Fragment {

    private Player player;

    private ProfileHeaderView header;
    private BadgeAdapter adp;

    public BadgesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_badges, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle extras = getArguments();
        player = (Player) extras.getSerializable("player");

        header = (ProfileHeaderView) getActivity().findViewById(R.id.header);
        header.setPlayer(player);

        adp = new BadgeAdapter(getActivity(), player);

        RecyclerView badgesLayout = (RecyclerView) getActivity().findViewById(R.id.badges_layout);
        badgesLayout.setAdapter(adp);
        badgesLayout.setItemAnimator(new DefaultItemAnimator());

        if (player.isBadgesLoaded()) {
            showPlayerBadges();
        } else {
            loadPlayerBadges();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.badges_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                loadPlayerBadges();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private class FragmentTask extends LoaderTask<MainActivity> {

        private Player player;

        private FragmentTask(MainActivity ctx, Player player) {
            super(ctx);

            this.player = player;
        }

        @Override
        public void process() {
            try {
                if (getContext().getPlayer().equals(player)) {
                    Util.getPlayerBadges(player);
                } else {
                    Ws.getPlayerBadges(player);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onComplete() {
            BadgesFragment.this.showPlayerBadges();
        }
    }

    private void loadPlayerBadges() {
        new FragmentTask((MainActivity) getActivity(), player);
    }

    private void showPlayerBadges() {
        header.refreshData();

        adp.clear();

        if (player.getPlayerBadges() != null) {
            for (PlayerBadge playerBadge : player.getPlayerBadges()) {
                adp.add(playerBadge.getBadge());
            }
        }

        adp.notifyDataSetChanged();
    }

}
