package com.x7.steambadger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

public abstract class AbstractOpenHelper extends OrmLiteSqliteOpenHelper {

	public AbstractOpenHelper(Context context, String DATABASE_NAME,
			int DATABASE_VERSION) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		createTables(db, connectionSource);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		dropTables(db, connectionSource);
		createTables(db, connectionSource);
	}

	public abstract void createTables(SQLiteDatabase db,
			ConnectionSource connectionSource);

	public abstract void dropTables(SQLiteDatabase db,
			ConnectionSource connectionSource);

	public abstract void clearTables(SQLiteDatabase db,
			ConnectionSource connectionSource);
}