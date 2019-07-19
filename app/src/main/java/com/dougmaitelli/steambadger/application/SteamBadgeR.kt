package com.dougmaitelli.steambadger.application

import android.app.Application

import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso

class SteamBadgeR : Application() {


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

}
