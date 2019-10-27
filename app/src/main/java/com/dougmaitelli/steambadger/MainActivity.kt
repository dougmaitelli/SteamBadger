package com.dougmaitelli.steambadger

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem

import com.j256.ormlite.dao.DaoManager
import com.dougmaitelli.steambadger.activity.LoginActivity
import com.dougmaitelli.steambadger.application.Config
import com.dougmaitelli.steambadger.application.SteamBadgeR
import com.dougmaitelli.steambadger.database.DatabaseHelper
import com.dougmaitelli.steambadger.database.model.Player
import com.dougmaitelli.steambadger.fragment.ProfileFragment
import com.dougmaitelli.steambadger.fragment.menu.MenuFragment
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {

    var player: Player? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null

    private var mMenu: Fragment? = null
    private var mContent: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        if (Config.getInstance(this).steamId.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            return
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        mDrawerLayout = findViewById(R.id.drawer_layout)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        mDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.menu, R.string.menu)

        mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)

        if (savedInstanceState != null) {
            mMenu = supportFragmentManager.getFragment(savedInstanceState, "mMenu")
            mContent = supportFragmentManager.getFragment(savedInstanceState, "mContent")
        }

        getLoggedUser()

        if (mMenu == null) {
            mMenu = MenuFragment()
        }

        if (mContent == null) {
            val bundle = Bundle()
            bundle.putSerializable("player", player)
            mContent = ProfileFragment()
            mContent!!.arguments = bundle
        }

        startView()
    }

    private fun startView() {
        // Menu
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.left_drawer, mMenu!!)
                .commit()

        // Content
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, mContent!!)
                .commit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        if (mDrawerToggle != null) {
            mDrawerToggle!!.syncState()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (mDrawerToggle != null) {
            mDrawerToggle!!.onConfigurationChanged(newConfig)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> this.toogleMenu()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun toogleMenu() {
        if (mDrawerToggle == null) {
            return
        }

        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            mDrawerLayout!!.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (mDrawerToggle == null) {
            return
        }

        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
            return
        }

        if (navigateBack()) {
            return
        }

        this.moveTaskToBack(true)
    }

    private fun navigateBack(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            mContent = supportFragmentManager.findFragmentById(R.id.content_frame)
            return true
        }

        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (mMenu != null) {
            supportFragmentManager.putFragment(outState, "mMenu", supportFragmentManager.findFragmentById(R.id.left_drawer)!!)
        }

        if (mContent != null) {
            supportFragmentManager.putFragment(outState, "mContent", supportFragmentManager.findFragmentById(R.id.content_frame)!!)
        }
    }

    fun logoff() {
        val customUrl = Config.getInstance(this).customUrl

        Config.getInstance(this).setSteamId(null)
        Config.getInstance(this).setCustomUrl(null)

        val bundle = Bundle()
        bundle.putString(SteamBadgeR.Param.CUSTOM_URL, customUrl)
        firebaseAnalytics.logEvent(SteamBadgeR.Event.LOGOUT, bundle)

        loginActivity()
    }

    private fun loginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        finish()
    }

    @JvmOverloads
    fun switchContent(fragment: Fragment, dontStore: Boolean = false) {
        mDrawerLayout!!.closeDrawer(GravityCompat.START)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.content_frame)

        if (currentFragment != null && currentFragment.javaClass === fragment.javaClass) {
            return
        }

        mContent = fragment

        val tx = supportFragmentManager.beginTransaction()

        tx.replace(R.id.content_frame, fragment)

        if (!dontStore) {
            tx.addToBackStack(null)
        }

        tx.commit()
    }

    private fun getLoggedUser() {
        try {
            val steamId = Config.getInstance(this).steamId

            val playerDao = DaoManager.createDao(DatabaseHelper.connectionSource, Player::class.java)
            val playerResult = playerDao.queryForEq("steamId", steamId)

            if (playerResult.isEmpty()) {
                player = Player()
                player!!.steamId = steamId
                playerDao.create(player)

                playerDao.refresh(player)
            } else {
                player = playerResult[0]
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

}
