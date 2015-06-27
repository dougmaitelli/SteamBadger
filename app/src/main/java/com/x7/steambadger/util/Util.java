package com.x7.steambadger.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.x7.steambadger.database.DbOpenHelper;
import com.x7.steambadger.database.model.Badge;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.database.model.PlayerBadge;
import com.x7.steambadger.ws.Ws;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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

    public static byte[] imageToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap byteArrayToImage(byte[] bytes) {
        return (BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }

    public static void saveLocalImage(Context context, String filename, Bitmap bitmap) {
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveLocalBadgeImage(Context context, Badge badge, Bitmap bitmap) {
        saveLocalImage(context, badge.getBadgeId() + "_" + badge.getAppId() + "_" + badge.getBorderColor() + "_" + badge.getLevel() + ".png", bitmap);
    }

    public static Bitmap openLocalImage(Context context, String filename) {
        FileInputStream inputStream;

        try {
            inputStream = context.openFileInput(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap openLocalBadgeImage(Context context, Badge badge) {
        return openLocalImage(context, badge.getBadgeId() + "_" + badge.getAppId() + "_" + badge.getBorderColor() + "_" + badge.getLevel() + ".png");
    }

}
