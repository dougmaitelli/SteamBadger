package com.dougmaitelli.steambadger.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.support.ConnectionSource

abstract class AbstractOpenHelper(context: Context, DATABASE_NAME: String,
                                  DATABASE_VERSION: Int) : OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase, connectionSource: ConnectionSource) {
        createTables(db, connectionSource)
    }

    override fun onUpgrade(db: SQLiteDatabase, connectionSource: ConnectionSource,
                  oldVersion: Int, newVersion: Int) {
        dropTables(db, connectionSource)
        createTables(db, connectionSource)
    }

    abstract fun createTables(db: SQLiteDatabase,
                              connectionSource: ConnectionSource)

    abstract fun dropTables(db: SQLiteDatabase,
                            connectionSource: ConnectionSource)

    abstract fun clearTables(db: SQLiteDatabase,
                             connectionSource: ConnectionSource)
}