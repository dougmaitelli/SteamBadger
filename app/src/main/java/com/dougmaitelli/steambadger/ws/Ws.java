package com.dougmaitelli.steambadger.ws;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.dougmaitelli.steambadger.database.DbOpenHelper;
import com.dougmaitelli.steambadger.database.model.Badge;
import com.dougmaitelli.steambadger.database.model.Player;
import com.dougmaitelli.steambadger.database.model.PlayerBadge;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

    public enum AvatarQuality {

        LOW,
        MEDIUM,
        HIGH
    }

    public static List<Player> getPlayerData(List<Player> players, AvatarQuality avatarQuality) throws Exception {
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
            player.setProfileUrl(playerObject.getString("profileurl"));

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
        }

        List<Player> playerList = new ArrayList<>(requestedPlayers.values());
        Collections.sort(playerList);

        return playerList;
    }

    public static List<Player> getPlayerFriends(Player player) throws Exception{
        HashMap<String, String> values = new HashMap<>();
        values.put("steamid", player.getSteamId());

        JSONObject object = sendRequest("ISteamUser/GetFriendList/v1/", values).getJSONObject("friendslist");

        JSONArray playerFriends = object.getJSONArray("friends");

        List<Player> friendList = new ArrayList<>();

        for (int i = 0; i < playerFriends.length(); i++) {
            JSONObject friendObject = playerFriends.getJSONObject(i);

            Player friend = new Player();
            friend.setSteamId(friendObject.getString("steamid"));

            friendList.add(friend);
        }

        return getPlayerData(friendList, AvatarQuality.HIGH);
    }

    public static Player getPlayerBadges(Player player) throws Exception {
        Dao<Badge, Long> badgeDao = DaoManager.createDao(DbOpenHelper.getCon(), Badge.class);
        Dao<PlayerBadge, Long> playerBadgeDao = DaoManager.createDao(DbOpenHelper.getCon(), PlayerBadge.class);

        HashMap<String, String> values = new HashMap<>();
        values.put("steamid", player.getSteamId());

        JSONObject object = sendRequest("IPlayerService/GetBadges/v1/", values).getJSONObject("response");

        player.setPlayerXp(object.getLong("player_xp"));
        player.setPlayerLevel(object.getInt("player_level"));
        player.setPlayerXpNeededToLevelUp(object.getLong("player_xp_needed_to_level_up"));
        player.setPlayerXpNeededCurrentLevel(object.getLong("player_xp_needed_current_level"));

        if (player.getId() == null) {
            player.getPlayerBadges().clear();
        } else {
            player.getPlayerBadgesCollection().clear();
        }

        JSONArray badgesArray = object.getJSONArray("badges");

        for (int i = 0; i < badgesArray.length(); i++) {
            JSONObject badgeObject = badgesArray.getJSONObject(i);

            Badge badge = new Badge();
            badge.setBadgeId(badgeObject.getInt("badgeid"));

            if (badgeObject.has("appid")) {
                badge.setAppId(badgeObject.getString("appid"));
                badge.setBorderColor(badgeObject.getInt("border_color"));
            }

            badge.setLevel(badgeObject.getInt("level"));

            List<Badge> badgesResult = badgeDao.queryForMatchingArgs(badge);

            if (badgesResult.size() > 0) {
                badge = badgesResult.get(0);
            } else {
                badgeDao.create(badge);
            }

            PlayerBadge playerBadge = new PlayerBadge();
            playerBadge.setPlayer(player);
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

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(requestUrl).build();
        Response response = client.newCall(request).execute();

        return new JSONObject(response.body().string());
    }

    public static void loadBadgeData(Player player, Badge badge) throws IOException {
        String url = "badges/" + badge.getBadgeId();

        if (badge.getAppId() != null) {
            url = "gamecards/" + badge.getAppId();
        }

        Document doc = Jsoup.connect(player.getProfileUrl() + "/" + url).get();

        Elements badgeContainer = doc.select(".badge_info");

        Element elementTitle = badgeContainer.select(".badge_info_title").get(0);
        badge.setText(elementTitle.text());

        Element elementImage = badgeContainer.select(".badge_info_image img").get(0);
        badge.setImageUrl(elementImage.attr("src"));
    }

}
