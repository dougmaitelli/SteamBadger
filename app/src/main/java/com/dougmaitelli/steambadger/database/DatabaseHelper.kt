package com.dougmaitelli.steambadger.database

import android.database.sqlite.SQLiteDatabase

import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.dougmaitelli.steambadger.application.SteamBadgeR
import com.dougmaitelli.steambadger.database.model.Badge
import com.dougmaitelli.steambadger.database.model.Player
import com.dougmaitelli.steambadger.database.model.PlayerBadge
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper

object DatabaseHelper : OrmLiteSqliteOpenHelper(SteamBadgeR.instance, "steam_badger", null, 1) {

    override fun onCreate(database: SQLiteDatabase?, connectionSource: ConnectionSource?) {
        TableUtils.createTableIfNotExists(connectionSource, Player::class.java)
        TableUtils.createTableIfNotExists(connectionSource, PlayerBadge::class.java)
        TableUtils.createTableIfNotExists(connectionSource, Badge::class.java)
    }

    override fun onUpgrade(database: SQLiteDatabase?, connectionSource: ConnectionSource?, oldVersion: Int, newVersion: Int) {
        TableUtils.dropTable<Player, Any>(connectionSource, Player::class.java, true)
        TableUtils.dropTable<PlayerBadge, Any>(connectionSource, PlayerBadge::class.java, true)
        TableUtils.dropTable<Badge, Any>(connectionSource, Badge::class.java, true)
        onCreate(database, connectionSource)
    }

}
