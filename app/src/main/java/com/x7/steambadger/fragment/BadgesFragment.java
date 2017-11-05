package com.x7.steambadger.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.x7.steambadger.MainActivity;
import com.x7.steambadger.R;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.database.model.PlayerBadge;
import com.x7.steambadger.util.LoaderTask;
import com.x7.steambadger.util.Util;
import com.x7.steambadger.view.ProfileHeaderView;
import com.x7.steambadger.view.adapter.BadgeAdapter;
import com.x7.steambadger.ws.Ws;

public class BadgesFragment extends Fragment {

    private Player player;

    private ProfileHeaderView header;
    private GridView badgesLayout;
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

        badgesLayout = (GridView) getActivity().findViewById(R.id.badges_layout);
        adp = new BadgeAdapter(getActivity(), player);

        badgesLayout.setAdapter(adp);

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

    private void loadPlayerBadges() {
        new LoaderTask<MainActivity>((MainActivity) getActivity(), true) {

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
                showPlayerBadges();
            }
        };
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
