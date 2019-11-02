package com.dougmaitelli.steambadger.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.dougmaitelli.steambadger.MainActivity
import com.dougmaitelli.steambadger.R
import com.dougmaitelli.steambadger.application.Config
import com.dougmaitelli.steambadger.application.Config.get
import com.dougmaitelli.steambadger.application.Config.set
import com.dougmaitelli.steambadger.application.SteamBadgeR
import com.dougmaitelli.steambadger.util.LoaderTask
import com.dougmaitelli.steambadger.ws.Ws
import com.google.firebase.analytics.FirebaseAnalytics

class LoginActivity : Activity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val customUrlEdit = findViewById<EditText>(R.id.custom_url)
        val loginButton = findViewById<Button>(R.id.login_button)

        customUrlEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButton.performClick()
                return@setOnEditorActionListener true
            }

            false
        }

        loginButton.setOnClickListener {
            val customUrl = customUrlEdit.text.toString()

            login(customUrl)
        }
    }

    private class LoginTask constructor(ctx: LoginActivity, internal var customUrl: String) : LoaderTask<LoginActivity>(ctx) {

        private var hasException: Exception? = null

        override fun process() {
            try {
                val steamId = Ws.getSteamId(customUrl)

                val prefs = Config.defaultPrefs(context)

                prefs[Config.SharedPrefs.CUSTOMURL] = customUrl
                prefs[Config.SharedPrefs.STEAMID] = steamId
            } catch (ex: Exception) {
                hasException = ex
            }

        }

        override fun onComplete() {
            if (hasException != null) {
                Toast.makeText(context, R.string.login_error, Toast.LENGTH_SHORT).show()
                return
            }

            val prefs = Config.defaultPrefs(context)

            val bundle = Bundle()
            bundle.putString(SteamBadgeR.Param.CUSTOM_URL, prefs[Config.SharedPrefs.CUSTOMURL])
            bundle.putString(SteamBadgeR.Param.STEAM_ID, prefs[Config.SharedPrefs.STEAMID])
            context.firebaseAnalytics.logEvent(SteamBadgeR.Event.LOGIN, bundle)

            val activity = Intent(context, MainActivity::class.java)
            context.startActivity(activity)
        }
    }

    private fun login(customUrl: String) {
        if (customUrl.isEmpty()) {
            return
        }

        LoginTask(this, customUrl)
    }

}

