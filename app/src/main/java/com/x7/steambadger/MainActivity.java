package com.x7.steambadger;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.x7.steambadger.activity.LoginActivity;
import com.x7.steambadger.application.Config;
import com.x7.steambadger.database.DbOpenHelper;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.fragment.ProfileFragment;
import com.x7.steambadger.fragment.menu.MenuFragment;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Player player;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Fragment mContent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Config.getInstance().getSteamId().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        //DbOpenHelper.getInstance().dropTables(null, DbOpenHelper.getCon());
        DbOpenHelper.getInstance().createTables(null, DbOpenHelper.getCon());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.menu, R.string.menu);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        }

        getLoggedUser();

        if (mContent == null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("player", player);
            mContent = new ProfileFragment();
            mContent.setArguments(bundle);
        }

        startView();
    }

    public Player getPlayer() {
        return player;
    }

    private void startView() {
        // Menu
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_drawer, new MenuFragment())
                .commit();

        // Content
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, mContent)
                .commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.toogleMenu();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void toogleMenu() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (navigateBack()) {
            return;
        }

        this.moveTaskToBack(true);
    }

    public boolean navigateBack() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            mContent = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            return true;
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mContent != null) {
            super.onSaveInstanceState(outState);

            getSupportFragmentManager().putFragment(outState, "mContent", getSupportFragmentManager().findFragmentById(R.id.content_frame));
        }
    }

    public void logoff() {
        Config.getInstance().setSteamId(null);
        Config.getInstance().setCustomUrl(null);
        loginActivity();
    }

    public void loginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        finish();
    }

    public void switchContent(Fragment fragment) {
        switchContent(fragment, false);
    }

    public void switchContent(Fragment fragment, boolean dontStore) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (currentFragment != null && currentFragment.getClass() == fragment.getClass()) {
            return;
        }

        mContent = fragment;

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();

        tx.replace(R.id.content_frame, fragment);

        if (!dontStore) {
            tx.addToBackStack(null);
        }

        tx.commit();
    }

    private void getLoggedUser() {
        try {
            String steamId = Config.getInstance().getSteamId();

            Dao<Player, Long> playerDao = DaoManager.createDao(DbOpenHelper.getCon(), Player.class);
            List<Player> playerResult = playerDao.queryForEq("steamId", steamId);

            if (playerResult.isEmpty()) {
                player = new Player();
                player.setSteamId(steamId);
                playerDao.create(player);

                playerDao.refresh(player);
            } else {
                player = playerResult.get(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
