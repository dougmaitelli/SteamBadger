package com.dougmaitelli.steambadger.application

import androidx.multidex.MultiDexApplication

import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso

class SteamBadgeR : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        val builder = Picasso.Builder(this)
        builder.downloader(OkHttpDownloader(this, Long.MAX_VALUE))
        val built = builder.build()
        Picasso.setSingletonInstance(built)
    }

    init {
        instance = this
    }

    companion object {
        lateinit var instance: SteamBadgeR
    }

    class Event private constructor() {

        companion object {
            const val LOGIN = "login"
            const val LOGOUT = "logout"
        }

    }

    class Param private constructor() {

        companion object {
            const val CUSTOM_URL = "customUrl"
            const val STEAM_ID = "steamId"
        }

    }

}
