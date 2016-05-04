package com.x7.steambadger.application;

import android.app.Application;

import com.splunk.mint.Mint;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class SteamBadgeR extends Application {

	private static SteamBadgeR singleton;

	public SteamBadgeR() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());
		SteamBadgeR.singleton = this;

		Mint.initAndStartSession(this, "78b121ad");
	}

	public static SteamBadgeR getInstance() {
		return SteamBadgeR.singleton;
	}

}
