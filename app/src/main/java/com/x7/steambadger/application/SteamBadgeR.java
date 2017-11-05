package com.x7.steambadger.application;

import android.app.Application;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class SteamBadgeR extends Application {

	private static SteamBadgeR singleton;

	public SteamBadgeR() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SteamBadgeR.singleton = this;

		Picasso.Builder builder = new Picasso.Builder(this);
		builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
		Picasso built = builder.build();
		Picasso.setSingletonInstance(built);
	}

	public static SteamBadgeR getInstance() {
		return SteamBadgeR.singleton;
	}

}
