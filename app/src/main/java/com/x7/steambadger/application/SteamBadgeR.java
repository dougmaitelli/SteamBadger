package com.x7.steambadger.application;

import android.app.Application;

public class SteamBadgeR extends Application {

	private static SteamBadgeR singleton;

	public SteamBadgeR() {

	}

	@Override
	public void onCreate() {
		super.onCreate();
		SteamBadgeR.singleton = this;
	}

	public static SteamBadgeR getInstance() {
		return SteamBadgeR.singleton;
	}

}
