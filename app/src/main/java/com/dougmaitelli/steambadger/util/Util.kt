package com.dougmaitelli.steambadger.util

import com.j256.ormlite.dao.DaoManager
import com.dougmaitelli.steambadger.database.DatabaseHelper
import com.dougmaitelli.steambadger.database.model.Player
import com.dougmaitelli.steambadger.database.model.PlayerBadge
import com.dougmaitelli.steambadger.ws.Ws

object Util {

    @Throws(Exception::class)
    fun getPlayerData(player: Player): Player {
        val playerDao = DaoManager.createDao(DatabaseHelper.connectionSource, Player::class.java)

        Ws.getPlayerData(player)

        playerDao.update(player)

        return player
    }

    @Throws(Exception::class)
    fun getPlayerBadges(player: Player): Player {
        val playerDao = DaoManager.createDao(DatabaseHelper.connectionSource, Player::class.java)
        val playerBadgeDao = DaoManager.createDao(DatabaseHelper.connectionSource, PlayerBadge::class.java)

        for (playerBadge in player.playerBadges) {
            playerBadgeDao.delete(playerBadge)
        }

        Ws.getPlayerBadges(player)

        player.isBadgesLoaded = true
        playerDao.update(player)

        return player
    }

}
