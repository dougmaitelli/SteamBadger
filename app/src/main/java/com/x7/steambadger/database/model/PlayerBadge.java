package com.x7.steambadger.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable
public class PlayerBadge implements Serializable {

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(foreign = true)
    private Player player;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Badge badge;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }
}
