package com.dougmaitelli.steambadger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.dougmaitelli.steambadger.application.SteamBadgeR;
import com.dougmaitelli.steambadger.database.model.Badge;
import com.dougmaitelli.steambadger.database.model.Player;
import com.dougmaitelli.steambadger.database.model.PlayerBadge;

import java.sql.SQLException;

public class DbOpenHelper extends AbstractOpenHelper {

	public static final String DATABASE_NAME = "steam_badger";
	public static int DATABASE_VERSION = 1;

	private static DbOpenHelper dbHelper;

	public static ConnectionSource getCon() {
		return getInstance().getConnectionSource();
	}

	public static DbOpenHelper getInstance() {
		if (dbHelper == null) {
			dbHelper = new DbOpenHelper(SteamBadgeR.getInstance());
		}
		return dbHelper;
	}

	public DbOpenHelper(Context context) {
		super(context, DATABASE_NAME, DATABASE_VERSION);
	}

	@Override
	public void createTables(SQLiteDatabase db,
			ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(getCon(), Player.class);
			TableUtils.createTable(getCon(), PlayerBadge.class);
			TableUtils.createTable(getCon(), Badge.class);
		} catch (SQLException e) {
			System.out.println("DB Create Error");
		}
	}

	@Override
	public void dropTables(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.dropTable(getCon(), Player.class, true);
			TableUtils.dropTable(getCon(), PlayerBadge.class, true);
			TableUtils.dropTable(getCon(), Badge.class, true);
		} catch (SQLException e) {
			System.out.println("DB Drop Error");
		}
	}

	@Override
	public void clearTables(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.clearTable(getCon(), Player.class);
			TableUtils.clearTable(getCon(), PlayerBadge.class);
			TableUtils.clearTable(getCon(), Badge.class);
		} catch (SQLException e) {
			System.out.println("DB Clear Error");
		}

	}

}
