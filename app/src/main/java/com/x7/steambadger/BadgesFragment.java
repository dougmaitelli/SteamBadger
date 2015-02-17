package com.x7.steambadger;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.x7.steambadger.database.DbOpenHelper;
import com.x7.steambadger.database.model.Badge;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.database.model.PlayerBadge;
import com.x7.steambadger.util.LoaderTask;
import com.x7.steambadger.view.BadgeView;
import com.x7.steambadger.view.FlowLayout;
import com.x7.steambadger.ws.Util;

import java.util.Collection;
import java.util.List;

public class BadgesFragment extends Fragment {

    private Player player;

    private FlowLayout badgesLayout;

    public BadgesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_badges, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        badgesLayout = (FlowLayout) getActivity().findViewById(R.id.badges_layout);

        try {
            String steamid = "76561198012101080";

            Dao<Player, Long> playerDao = DaoManager.createDao(DbOpenHelper.getCon(), Player.class);
            List<Player> playerResult = playerDao.queryForEq("steamId", steamid);

            if (playerResult.isEmpty()) {
                player = new Player();
                player.setSteamId(steamid);
                playerDao.create(player);

                playerResult = playerDao.queryForEq("steamId", steamid);
            }

            player = playerResult.get(0);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }

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
                    Util.getPlayerBadges(player);
                } catch (Exception ex) {
                    ex.printStackTrace(System.out);
                }
            }

            @Override
            public void onComplete() {
                showPlayerBadges();
            }
        };
    }

    private void showPlayerBadges() {
        new LoaderTask<MainActivity>((MainActivity) getActivity(), true) {

            @Override
            public void process() {
                try {
                    Dao<Badge, Long> badgeDao = DaoManager.createDao(DbOpenHelper.getCon(), Badge.class);

                    ForeignCollection<PlayerBadge> playerBadges = player.getPlayerBadges();

                    int badgeCount = 0;
                    for (PlayerBadge playerBadge : playerBadges) {
                        Badge badge = playerBadge.getBadge();
                        badgeCount++;

                        this.doUpdate(badgeCount, playerBadges.size());

                        if (badge == null) {
                            try {
                                badge = Util.loadBadgeData(playerBadge.getAppId(), playerBadge.getBadgeId(), playerBadge.getLevel());
                                badgeDao.create(badge);

                                playerBadge.setBadge(badge);
                                playerBadges.update(playerBadge);

                                Bitmap bitmap = Util.getRemoteImage(badge.getImageUrl());
                                Util.saveLocalBadgeImage(getActivity(), badge, bitmap);
                            } catch (Exception ex) {
                                ex.printStackTrace(System.out);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(System.out);
                }
            }

            @Override
            public void onUpdate(Object... values) {
                this.dialog.setMessage("Loading Badge " + values[0] + "/" + values[1]);
            }

            @Override
            public void onComplete() {
                badgesLayout.removeAllViews();

                Collection<PlayerBadge> playerBadges = player.getPlayerBadges();

                for (PlayerBadge playerBadge : playerBadges) {
                    Badge badge = playerBadge.getBadge();

                    if (badge == null) {
                        continue;
                    }

                    BadgeView badgeView = new BadgeView(getContext(), badge);
                    badgesLayout.addView(badgeView);
                }
            }
        };
    }

}
