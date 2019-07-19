package com.dougmaitelli.steambadger.application

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.dougmaitelli.steambadger.util.SingletonHolder

class Config private constructor(context: Context) {

    private val CFG_KEY_CUSTOMURL = "K_CUSTOMURL"
    private val CFG_KEY_STEAMID = "K_STEAMID"

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(SteamBadgeR.instance)

    val customUrl: String?
        get() = preferences.getString(CFG_KEY_CUSTOMURL, "")

    val steamId: String?
        get() = preferences.getString(CFG_KEY_STEAMID, "")

    fun setCustomUrl(customUrl: String?): Boolean {
        val ed = preferences.edit()
        ed.putString(CFG_KEY_CUSTOMURL, customUrl)
        return ed.commit()
    }

    fun setSteamId(steamId: String?): Boolean {
        val ed = preferences.edit()
        ed.putString(CFG_KEY_STEAMID, steamId)
        return ed.commit()
    }

    companion object : SingletonHolder<Config, Context>(::Config)

}
