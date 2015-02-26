package com.x7.steambadger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.x7.steambadger.activity.LoginActivity;
import com.x7.steambadger.application.Config;
import com.x7.steambadger.database.DbOpenHelper;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.fragment.BadgesFragment;
import com.x7.steambadger.fragment.menu.NavigationDrawerFragment;
import com.x7.steambadger.util.LoaderTask;
import com.x7.steambadger.ws.Util;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private Player player;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Config.getInstance().getSteamId().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            finish();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        //DbOpenHelper.getInstance().dropTables(null, DbOpenHelper.getCon());
        DbOpenHelper.getInstance().createTables(null, DbOpenHelper.getCon());

        try {
            String steamId = Config.getInstance().getSteamId();

            Dao<Player, Long> playerDao = DaoManager.createDao(DbOpenHelper.getCon(), Player.class);
            List<Player> playerResult = playerDao.queryForEq("steamId", steamId);

            if (playerResult.isEmpty()) {
                player = new Player();
                player.setSteamId(steamId);
                playerDao.create(player);

                playerDao.refresh(player);

                loadPlayerData();
            } else {
                player = playerResult.get(0);

                startMainFragment();
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private void loadPlayerData() {
        new LoaderTask<MainActivity>(this, true) {

            @Override
            public void process() {
                try {
                    Util.getPlayerData(player);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }

            @Override
            public void onComplete() {
                startMainFragment();
            }
        };
    }

    private void startMainFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("player", player);
        Fragment fragment = new BadgesFragment();
        fragment.setArguments(bundle);
        setFragment(fragment);
    }

}
