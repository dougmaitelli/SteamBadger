package com.x7.steambadger;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.util.Collection;
import java.util.List;

public class BadgesFragment extends Fragment {

    private Player player;

    private PredicateLayout badgesLayout;

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

        badgesLayout = (PredicateLayout) getActivity().findViewById(R.id.badges_layout);

        try {
            String steamid = "76561198012101080";

            Dao<Player, Long> playerDao = DaoManager.createDao(DbOpenHelper.getCon(), Player.class);
            List<Player> playerResult = playerDao.queryForEq("steamId", steamid);

            if (playerResult.isEmpty()) {
                player = new Player();
                player.setSteamId(steamid);
                playerDao.create(player);

                player = playerDao.queryForEq("steamId", steamid).get(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }

        if (player.isBadgesLoaded()) {
            showPlayerBadges();
        } else {
            loadPlayerBadges();
        }
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
                    Dao<PlayerBadge, Long> playerBadgeDao = DaoManager.createDao(DbOpenHelper.getCon(), PlayerBadge.class);
                    Dao<Badge, Long> badgeDao = DaoManager.createDao(DbOpenHelper.getCon(), Badge.class);

                    Collection<PlayerBadge> playerBadges = player.getPlayerBadges();

                    int badgeCount = 0;
                    for (PlayerBadge playerBadge : playerBadges) {
                        Badge badge = playerBadge.getBadge();
                        badgeCount++;

                        if (badge == null) {
                            this.dialog.setMessage("Loading Badge " + badgeCount + "/" + playerBadges.size());

                            badge = Util.loadBadgeData(playerBadge.getAppId(), playerBadge.getBadgeId(), playerBadge.getLevel());
                            badgeDao.create(badge);

                            playerBadge.setBadge(badge);
                            playerBadgeDao.update(playerBadge);

                            Bitmap bitmap = Util.getRemoteImage(badge.getImageUrl());
                            Util.saveLocalBadgeImage(getActivity(), badge, bitmap);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(System.out);
                }
            }

            @Override
            public void onComplete() {
                Collection<PlayerBadge> playerBadges = player.getPlayerBadges();

                for (PlayerBadge playerBadge : playerBadges) {
                    Badge badge = playerBadge.getBadge();

                    ImageView badgeView = new ImageView(getContext());
                    badgeView.setImageBitmap(Util.openLocalBadgeImage(getContext(), badge));

                    badgesLayout.addView(badgeView);
                }
            }
        };
    }

}
