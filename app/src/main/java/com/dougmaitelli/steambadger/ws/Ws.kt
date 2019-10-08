package com.dougmaitelli.steambadger.ws

import com.j256.ormlite.dao.DaoManager
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.dougmaitelli.steambadger.database.DatabaseHelper
import com.dougmaitelli.steambadger.database.model.Badge
import com.dougmaitelli.steambadger.database.model.Player
import com.dougmaitelli.steambadger.database.model.PlayerBadge

import org.json.JSONObject
import org.jsoup.Jsoup

import java.io.IOException
import java.util.*

object Ws {

    private val API_KEY = "27D8D587593B6BA04261A296F62DADAB"
    private val API_URL = "https://api.steampowered.com/"

    fun getSteamId(customUrl: String): String {
        val values = HashMap<String, String>()
        values["vanityurl"] = customUrl

        val jsonObject = sendRequest("ISteamUser/ResolveVanityURL/v1/", values).getJSONObject("response")

        return jsonObject.getString("steamid")
    }

    fun getPlayerData(player: Player): Player {
        val players = ArrayList<Player>()
        players.add(player)

        return getPlayerData(players, AvatarQuality.HIGH)[0]
    }

    enum class AvatarQuality {
        LOW,
        MEDIUM,
        HIGH
    }

    private fun getPlayerData(players: List<Player>, avatarQuality: AvatarQuality): List<Player> {
        val requestedPlayers = HashMap<String, Player>()

        val sb = StringBuilder()

        for (player in players) {
            requestedPlayers[player.steamId!!] = player

            sb.append(player.steamId).append(",")
        }

        val values = HashMap<String, String>()
        values.put("steamids", sb.toString())

        val `object` = sendRequest("ISteamUser/GetPlayerSummaries/v0002/", values).getJSONObject("response")

        val playersArray = `object`.getJSONArray("players")

        for (i in 0 until playersArray.length()) {
            val playerObject = playersArray.getJSONObject(i)

            val player = requestedPlayers[playerObject.getString("steamid")]

            player!!.name = playerObject.getString("personaname")
            player.profileUrl = playerObject.getString("profileurl")

            val avatarAttr: String
            when (avatarQuality) {
                Ws.AvatarQuality.LOW -> avatarAttr = "avatar"
                Ws.AvatarQuality.MEDIUM -> avatarAttr = "avatarmedium"
                Ws.AvatarQuality.HIGH -> avatarAttr = "avatarfull"
            }

            player.avatarUrl = playerObject.getString(avatarAttr)
        }

        val playerList = ArrayList(requestedPlayers.values)
        playerList.sort()

        return playerList
    }

    fun getPlayerFriends(player: Player): List<Player> {
        val values = HashMap<String, String>()
        values.put("steamid", player.steamId!!)

        val `object` = sendRequest("ISteamUser/GetFriendList/v1/", values).getJSONObject("friendslist")

        val playerFriends = `object`.getJSONArray("friends")

        val friendList = ArrayList<Player>()

        for (i in 0 until playerFriends.length()) {
            val friendObject = playerFriends.getJSONObject(i)

            val friend = Player()
            friend.steamId = friendObject.getString("steamid")

            friendList.add(friend)
        }

        return getPlayerData(friendList, AvatarQuality.HIGH)
    }

    fun getPlayerBadges(player: Player): Player {
        val badgeDao = DaoManager.createDao(DatabaseHelper.connectionSource, Badge::class.java)

        val values = HashMap<String, String>()
        values.put("steamid", player.steamId!!)

        val jsonObject = sendRequest("IPlayerService/GetBadges/v1/", values).getJSONObject("response")

        player.playerXp = jsonObject.getLong("player_xp")
        player.playerLevel = jsonObject.getInt("player_level")
        player.playerXpNeededToLevelUp = jsonObject.getLong("player_xp_needed_to_level_up")
        player.playerXpNeededCurrentLevel = jsonObject.getLong("player_xp_needed_current_level")

        if (player.id == null) {
            player.playerBadges.clear()
        } else {
            player.playerBadgesCollection!!.clear()
        }

        val badgesArray = jsonObject.getJSONArray("badges")

        for (i in 0 until badgesArray.length()) {
            val badgeObject = badgesArray.getJSONObject(i)

            var badge = Badge()
            badge.badgeId = badgeObject.getInt("badgeid")

            if (badgeObject.has("appid")) {
                badge.appId = badgeObject.getString("appid")
                badge.borderColor = badgeObject.getInt("border_color")
            }

            badge.level = badgeObject.getInt("level")

            val badgesResult = badgeDao.queryForMatchingArgs(badge)

            if (badgesResult.size > 0) {
                badge = badgesResult.get(0)
            } else {
                badgeDao.create(badge)
            }

            val playerBadge = PlayerBadge()
            playerBadge.player = player
            playerBadge.badge = badge

            if (player.id == null) {
                player.playerBadges.add(playerBadge)
            } else {
                player.playerBadgesCollection!!.add(playerBadge)
            }
        }

        return player
    }

    private fun sendRequest(method: String, values: HashMap<String, String>): JSONObject {
        var requestUrl = "$API_URL$method?key=$API_KEY"

        for (key in values.keys) {
            requestUrl += "&" + key + "=" + values[key]
        }

        val client = OkHttpClient()
        val request = Request.Builder().url(requestUrl).build()
        val response = client.newCall(request).execute()

        return JSONObject(response.body().string())
    }

    fun loadBadgeData(player: Player, badge: Badge) {
        var url = "badges/" + badge.badgeId

        if (badge.appId != null) {
            url = "gamecards/" + badge.appId
        }

        val doc = Jsoup.connect(player.profileUrl + "/" + url).get()

        val badgeContainer = doc.select(".badge_info")

        val elementTitle = badgeContainer.select(".badge_info_title")[0]
        badge.text = elementTitle.text()

        val elementImage = badgeContainer.select(".badge_info_image img")[0]
        badge.imageUrl = elementImage.attr("src")
    }

}
