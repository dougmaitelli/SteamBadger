package com.x7.steambadger.ws;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.x7.steambadger.application.Config;
import com.x7.steambadger.database.DbOpenHelper;
import com.x7.steambadger.database.model.Badge;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.database.model.PlayerBadge;
import com.x7.steambadger.util.Util;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ws {

    private static final String API_KEY = "27D8D587593B6BA04261A296F62DADAB";
    private static final String API_URL = "https://api.steampowered.com/";

    public static String getSteamId(String customUrl) throws Exception {
        HashMap<String, String> values = new HashMap<>();
        values.put("vanityurl", customUrl);

        JSONObject object = sendRequest("ISteamUser/ResolveVanityURL/v1/", values).getJSONObject("response");

        return object.getString("steamid");
    }

    public static Player getPlayerData(Player player) throws Exception {
        List<Player> players = new ArrayList<>();
        players.add(player);

        return getPlayerData(players, AvatarQuality.HIGH).get(0);
    }

    public static enum AvatarQuality {

        LOW,
        MEDIUM,
        HIGH;

    }

    public static List<Player> getPlayerData(List<Player> players, AvatarQuality avatarQuality) throws Exception {
        Dao<Player, Long> playerDao = DaoManager.createDao(DbOpenHelper.getCon(), Player.class);

        HashMap<String, Player> requestedPlayers = new HashMap<>();

        StringBuilder sb = new StringBuilder();

        for (Player player : players) {
            requestedPlayers.put(player.getSteamId(), player);

            sb.append(player.getSteamId()).append(",");
        }

        HashMap<String, String> values = new HashMap<>();
        values.put("steamids", sb.toString());

        JSONObject object = sendRequest("ISteamUser/GetPlayerSummaries/v0002/", values).getJSONObject("response");

        JSONArray playersArray = object.getJSONArray("players");

        for (int i = 0; i < playersArray.length(); i++) {
            JSONObject playerObject = playersArray.getJSONObject(i);

            Player player = requestedPlayers.get(playerObject.getString("steamid"));

            player.setName(playerObject.getString("personaname"));

            String avatarAttr;
            switch (avatarQuality) {
                case LOW:
                    avatarAttr = "avatar";
                    break;
                case MEDIUM:
                    avatarAttr = "avatarmedium";
                    break;
                default:
                case HIGH:
                    avatarAttr = "avatarfull";
                    break;
            }

            player.setAvatarUrl(playerObject.getString(avatarAttr));
            player.setAvatar(Util.imageToByteArray(getRemoteImage(player.getAvatarUrl())));
        }

        List<Player> playerList = new ArrayList(requestedPlayers.values());

        Collections.sort(playerList);

        return playerList;
    }

    public static List<Player> getPlayerFriends(Player player) throws Exception{
        HashMap<String, String> values = new HashMap<>();
        values.put("steamid", player.getSteamId().toString());

        JSONObject object = sendRequest("ISteamUser/GetFriendList/v1/", values).getJSONObject("friendslist");

        JSONArray playerFriends = object.getJSONArray("friends");

        List<Player> friendList = new ArrayList<>();

        for (int i = 0; i < playerFriends.length(); i++) {
            JSONObject friendObject = playerFriends.getJSONObject(i);

            Player friend = new Player();
            friend.setSteamId(friendObject.getString("steamid"));

            friendList.add(friend);
        }

        return getPlayerData(friendList, AvatarQuality.LOW);
    }

    public static Player getPlayerBadges(Player player) throws Exception {
        Dao<Badge, Long> badgeDao = DaoManager.createDao(DbOpenHelper.getCon(), Badge.class);

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

            Badge badgeQuery = new Badge();
            badgeQuery.setBadgeId(badgeObject.getInt("badgeid"));

            if (badgeObject.has("appid")) {
                badgeQuery.setAppId(badgeObject.getString("appid"));
                badgeQuery.setBorderColor(badgeObject.getInt("border_color"));
            }

            badgeQuery.setLevel(badgeObject.getInt("level"));

            List<Badge> badgesResult = badgeDao.queryForMatchingArgs(badgeQuery);

            Badge badge = null;
            if (badgesResult.size() > 0) {
                badge = badgesResult.get(0);
            }

            PlayerBadge playerBadge = new PlayerBadge();
            playerBadge.setAppId(badgeQuery.getAppId());
            playerBadge.setBadgeId(badgeQuery.getBadgeId());
            playerBadge.setBorderColor(badgeQuery.getBorderColor());
            playerBadge.setLevel(badgeQuery.getLevel());
            playerBadge.setBadge(badge);

            if (player.getId() == null) {
                player.getPlayerBadges().add(playerBadge);
            } else {
                player.getPlayerBadgesCollection().add(playerBadge);
            }
        }

        return player;
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

        if (appId != null) {
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
        } else {
            Document doc = Jsoup.connect("http://steamcommunity.com/id/" + Config.getInstance().getCustomUrl() + "/badges/" + badgeId).get();

            Elements badgeContainer = doc.select(".badge_info");

            badge = new Badge();
            badge.setAppId(appId);
            badge.setBadgeId(badgeId);
            badge.setBorderColor(borderColor);
            badge.setLevel(level);

            Element elementTitle = badgeContainer.select(".badge_info_title").get(0);
            badge.setText(elementTitle.text());

            Element elementImage = badgeContainer.select(".badge_info_image img").get(0);
            badge.setImageUrl(elementImage.attr("src"));
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
            Logger.getLogger(Ws.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
