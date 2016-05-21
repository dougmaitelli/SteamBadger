package com.x7.steambadger.util;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.x7.steambadger.database.DbOpenHelper;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.database.model.PlayerBadge;
import com.x7.steambadger.ws.Ws;

public class Util {

    public static Player getPlayerData(Player player) throws Exception {
        Dao<Player, Long> playerDao = DaoManager.createDao(DbOpenHelper.getCon(), Player.class);

        Ws.getPlayerData(player);

        playerDao.update(player);

        return player;
    }

    public static Player getPlayerBadges(Player player) throws Exception {
        Dao<Player, Long> playerDao = DaoManager.createDao(DbOpenHelper.getCon(), Player.class);
        Dao<PlayerBadge, Long> playerBadgeDao = DaoManager.createDao(DbOpenHelper.getCon(), PlayerBadge.class);

        for (PlayerBadge playerBadge : player.getPlayerBadges()) {
            playerBadgeDao.delete(playerBadge);
        }

        Ws.getPlayerBadges(player);

        player.setBadgesLoaded(true);
        playerDao.update(player);

        return player;
    }

}
