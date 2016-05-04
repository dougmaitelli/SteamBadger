package com.x7.steambadger.database.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@DatabaseTable
public class Player implements Comparable, Serializable {

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(canBeNull = false)
    private String steamId;
    @DatabaseField
    private String name;
    @DatabaseField
    private String avatarUrl;
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] avatar;
    @DatabaseField(canBeNull = false)
    private long playerXp;
    @DatabaseField(canBeNull = false)
    private int playerLevel;
    @DatabaseField(canBeNull = false)
    private long playerXpNeededToLevelUp;
    @DatabaseField(canBeNull = false)
    private long playerXpNeededCurrentLevel;

    @DatabaseField
    private boolean badgesLoaded = false;

    @ForeignCollectionField(eager = false)
    private ForeignCollection<PlayerBadge> playerBadgesCollection;
    private List<PlayerBadge> playerBadges = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSteamId() {
        return steamId;
    }

    public void setSteamId(String steamId) {
        this.steamId = steamId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPlayerXp() {
        return playerXp;
    }

    public void setPlayerXp(long playerXp) {
        this.playerXp = playerXp;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(int playerLevel) {
        this.playerLevel = playerLevel;
    }

    public long getPlayerXpNeededToLevelUp() {
        return playerXpNeededToLevelUp;
    }

    public void setPlayerXpNeededToLevelUp(long playerXpNeededToLevelUp) {
        this.playerXpNeededToLevelUp = playerXpNeededToLevelUp;
    }

    public long getPlayerXpNeededCurrentLevel() {
        return playerXpNeededCurrentLevel;
    }

    public void setPlayerXpNeededCurrentLevel(long playerXpNeededCurrentLevel) {
        this.playerXpNeededCurrentLevel = playerXpNeededCurrentLevel;
    }

    public boolean isBadgesLoaded() {
        return badgesLoaded;
    }

    public void setBadgesLoaded(boolean badgesLoaded) {
        this.badgesLoaded = badgesLoaded;
    }

    public ForeignCollection<PlayerBadge> getPlayerBadgesCollection() {
        return playerBadgesCollection;
    }

    public void setPlayerBadgesCollection(ForeignCollection<PlayerBadge> playerBadgesCollection) {
        this.playerBadgesCollection = playerBadgesCollection;
    }

    public List<PlayerBadge> getPlayerBadges() {
        if (playerBadgesCollection != null && !playerBadgesCollection.isEmpty()) {
            playerBadges = new ArrayList<>(playerBadgesCollection);
        }

        return playerBadges;
    }

    public void setPlayerBadges(List<PlayerBadge> playerBadges) {
        this.playerBadges = playerBadges;
    }

    @Override
    public int compareTo(Object another) {
        return name.compareTo(((Player) another).getName());
    }
}
