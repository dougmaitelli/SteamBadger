package com.x7.steambadger.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.x7.steambadger.MainActivity;
import com.x7.steambadger.R;
import com.x7.steambadger.database.DbOpenHelper;
import com.x7.steambadger.database.model.Badge;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.database.model.PlayerBadge;
import com.x7.steambadger.util.LevelColor;
import com.x7.steambadger.util.LoaderTask;
import com.x7.steambadger.view.adapter.BadgeAdapter;
import com.x7.steambadger.ws.Util;

import java.util.Collection;

public class BadgesFragment extends Fragment {

    private Player player;

    private ImageView avatar;
    private TextView name;
    private ProgressBar levelProgress;
    private TextView playerExp;
    private TextView level;

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

        avatar = (ImageView) getActivity().findViewById(R.id.player_photo);
        name = (TextView) getActivity().findViewById(R.id.player_name);
        levelProgress = (ProgressBar) getActivity().findViewById(R.id.level_progress);
        playerExp = (TextView) getActivity().findViewById(R.id.player_exp);
        level = (TextView) getActivity().findViewById(R.id.level);

        badgesLayout = (GridView) getActivity().findViewById(R.id.badges_layout);
        adp = new BadgeAdapter(getActivity());

        badgesLayout.setAdapter(adp);

        Bundle extras = getArguments();
        player = (Player) extras.getSerializable("player");

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

                        try {
                            if (badge == null) {
                                badge = Util.loadBadgeData(playerBadge.getAppId(), playerBadge.getBadgeId(), playerBadge.getLevel());
                                badgeDao.create(badge);

                                playerBadge.setBadge(badge);
                                playerBadges.update(playerBadge);
                            }

                            Bitmap badgeImage = Util.openLocalBadgeImage(getContext(), badge);

                            if (badgeImage == null) {
                                Bitmap bitmap = Util.getRemoteImage(badge.getImageUrl());
                                Util.saveLocalBadgeImage(getContext(), badge, bitmap);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace(System.out);
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
                avatar.setImageBitmap(Util.byteArrayToImage(player.getAvatar()));
                name.setText(player.getName());
                levelProgress.setProgress((int) ((double) (player.getPlayerXp() - player.getPlayerXpNeededCurrentLevel()) / (double) (player.getPlayerXp() - player.getPlayerXpNeededCurrentLevel() + player.getPlayerXpNeededToLevelUp()) * 100));
                playerExp.setText("XP: " + player.getPlayerXp());
                level.setText(String.valueOf(player.getPlayerLevel()));
                level.getBackground().setLevel(player.getPlayerLevel());
                ((GradientDrawable) level.getBackground().getCurrent()).setColor(LevelColor.getLevelColor(player.getPlayerLevel()).getColor());

                adp.clear();

                Collection<PlayerBadge> playerBadges = player.getPlayerBadges();

                for (PlayerBadge playerBadge : playerBadges) {
                    Badge badge = playerBadge.getBadge();

                    if (badge == null) {
                        continue;
                    }

                    adp.add(badge);
                }

                adp.notifyDataSetChanged();
            }
        };
    }

}
