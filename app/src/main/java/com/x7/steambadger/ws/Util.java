package com.x7.steambadger.ws;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.x7.steambadger.database.DbOpenHelper;
import com.x7.steambadger.database.model.Badge;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.database.model.PlayerBadge;
import com.x7.steambadger.ws.conn.DataLoader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    private static final String API_KEY = "27D8D587593B6BA04261A296F62DADAB";
    private static final String API_URL = "https://api.steampowered.com/";

    public static String getSteamId(String customUrl) throws Exception {
        HashMap<String, String> values = new HashMap<>();
        values.put("vanityurl", customUrl);

        JSONObject object = sendRequest("ISteamUser/ResolveVanityURL/v1/", values).getJSONObject("response");

        return object.getString("steamid");
    }

    public static void getPlayerData(Player player) throws Exception {
        Dao<Player, Long> playerDao = DaoManager.createDao(DbOpenHelper.getCon(), Player.class);

        HashMap<String, String> values = new HashMap<>();
        values.put("steamids", player.getSteamId().toString());

        JSONObject object = sendRequest("ISteamUser/GetPlayerSummaries/v0002/", values).getJSONObject("response");

        JSONObject playerObject = object.getJSONArray("players").getJSONObject(0);

        player.setName(playerObject.getString("personaname"));
        player.setAvatarUrl(playerObject.getString("avatarfull"));
        player.setAvatar(imageToByteArray(getRemoteImage(player.getAvatarUrl())));

        playerDao.update(player);
    }

    public static void getPlayerBadges(Player player) throws Exception {
        Dao<Player, Long> playerDao = DaoManager.createDao(DbOpenHelper.getCon(), Player.class);
        Dao<PlayerBadge, Long> playerBadgeDao = DaoManager.createDao(DbOpenHelper.getCon(), PlayerBadge.class);
        Dao<Badge, Long> badgeDao = DaoManager.createDao(DbOpenHelper.getCon(), Badge.class);

        for (PlayerBadge playerBadge : player.getPlayerBadges()) {
            playerBadgeDao.delete(playerBadge);
        }

        HashMap<String, String> values = new HashMap<>();
        values.put("steamid", player.getSteamId().toString());

        JSONObject object = sendRequest("IPlayerService/GetBadges/v1/", values).getJSONObject("response");

        player.setPlayerXp(object.getLong("player_xp"));
        player.setPlayerLevel(object.getInt("player_level"));
        player.setPlayerXpNeededToLevelUp(object.getLong("player_xp_needed_to_level_up"));
        player.setPlayerXpNeededCurrentLevel(object.getLong("player_xp_needed_current_level"));

        JSONArray badgesArray = object.getJSONArray("badges");

        for (int i = 0; i < badgesArray.length(); i++) {
            JSONObject badgeObject = badgesArray.getJSONObject(i);

            if (!badgeObject.has("appid")) {
                continue;
            }

            Badge badgeQuery = new Badge();
            badgeQuery.setAppId(badgeObject.getString("appid"));
            badgeQuery.setBadgeId(badgeObject.getInt("badgeid"));
            badgeQuery.setBorderColor(badgeObject.getInt("border_color"));
            badgeQuery.setLevel(badgeObject.getInt("level"));

            List<Badge> badgesResult = badgeDao.queryForMatchingArgs(badgeQuery);

            Badge badge = null;
            if (badgesResult.size() > 0) {
                badge = badgesResult.get(0);
            }

            PlayerBadge playerBadge = new PlayerBadge();
            playerBadge.setPlayer(player);
            playerBadge.setAppId(badgeQuery.getAppId());
            playerBadge.setBadgeId(badgeQuery.getBadgeId());
            playerBadge.setBorderColor(badgeQuery.getBorderColor());
            playerBadge.setLevel(badgeQuery.getLevel());
            playerBadge.setBadge(badge);
            playerBadgeDao.create(playerBadge);
        }

        player.setBadgesLoaded(true);
        playerDao.update(player);
    }

    private static JSONObject sendRequest(String method, HashMap<String, String> values) throws Exception {
        String requestUrl = API_URL + method + "?key=" + API_KEY;

        for (String key : values.keySet()) {
            requestUrl += "&" + key + "=" + values.get(key);
        }

        DataLoader dl = new DataLoader();
        HttpResponse response = dl.secureLoadData(requestUrl);

        return new JSONObject(EntityUtils.toString(response.getEntity()));
    }

    public static Badge loadBadgeData(String appId, int badgeId, int borderColor, int level) throws IOException {
        Badge badge = null;

        Document doc = Jsoup.connect("http://www.steamcardexchange.net/index.php?gamepage-appid-" + appId).get();

        Elements badgeContainers = doc.select(".showcase-element-container.badge");
        Element badgeContainer = badgeContainers.get(borderColor);

        Elements showcaseElements = badgeContainer.children();

        for (Element showcaseElement : showcaseElements) {
            if (!showcaseElement.hasText()) {
                continue;
            }

            Element elementExperience = showcaseElement.select(".element-experience").get(0);

            if (elementExperience.text().contains("Level " + level)) {
                badge = new Badge();
                badge.setAppId(appId);
                badge.setBadgeId(badgeId);
                badge.setBorderColor(borderColor);
                badge.setLevel(level);

                Element elementText = showcaseElement.select(".element-text").get(0);
                badge.setText(elementText.text());

                Element elementImage = showcaseElement.select(".element-image").get(0);
                badge.setImageUrl(elementImage.attr("src"));

                break;
            }
        }

        return badge;
    }

    public static Bitmap getRemoteImage(String url) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(URI.create(url));
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream inStream = bufHttpEntity.getContent();

            Bitmap bm = BitmapFactory.decodeStream(inStream);

            inStream.close();

            return bm;
        } catch (Exception ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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
        saveLocalImage(context, badge.getAppId() + "_" + badge.getBorderColor() + "_" + badge.getLevel() + ".png", bitmap);
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
        return openLocalImage(context, badge.getAppId() + "_" + badge.getBorderColor() + "_" + badge.getLevel() + ".png");
    }

}
