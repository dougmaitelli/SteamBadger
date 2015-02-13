package com.x7.steambadger;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Player {

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(canBeNull = false)
    private String steamId;
    @DatabaseField
    private boolean badgesLoaded = false;
    @ForeignCollectionField(eager = false)
    private ForeignCollection<PlayerBadge> playerBadges;

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

    public boolean isBadgesLoaded() {
        return badgesLoaded;
    }

    public void setBadgesLoaded(boolean badgesLoaded) {
        this.badgesLoaded = badgesLoaded;
    }

    public ForeignCollection<PlayerBadge> getPlayerBadges() {
        return playerBadges;
    }

    public void setPlayerBadges(ForeignCollection<PlayerBadge> playerBadges) {
        this.playerBadges = playerBadges;
    }
}
