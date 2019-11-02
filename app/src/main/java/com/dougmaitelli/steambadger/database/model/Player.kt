package com.dougmaitelli.steambadger.database.model

import com.j256.ormlite.dao.ForeignCollection
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import java.io.Serializable

import java.util.ArrayList

@DatabaseTable
data class Player(

    @DatabaseField(generatedId = true)
    var id: Long? = null,
    @DatabaseField(canBeNull = false)
    var steamId: String? = null,
    @DatabaseField
    var name: String? = null,
    @DatabaseField
    var profileUrl: String? = null,
    @DatabaseField
    var avatarUrl: String? = null,
    @DatabaseField(canBeNull = false)
    var playerXp: Long = 0,
    @DatabaseField(canBeNull = false)
    var playerLevel: Int = 0,
    @DatabaseField(canBeNull = false)
    var playerXpNeededToLevelUp: Long = 0,
    @DatabaseField(canBeNull = false)
    var playerXpNeededCurrentLevel: Long = 0
) : Comparable<Player>, Serializable {

    @DatabaseField
    var isBadgesLoaded: Boolean = false

    @ForeignCollectionField(eager = false)
    var playerBadgesCollection: ForeignCollection<PlayerBadge>? = null
    var playerBadges: MutableList<PlayerBadge> = ArrayList()
        get() {
            if (playerBadgesCollection != null && !playerBadgesCollection!!.isEmpty()) {
                this.playerBadges = ArrayList(playerBadgesCollection!!)
            }

            return field
        }

    override operator fun compareTo(other: Player): Int {
        return name!!.compareTo(other.name!!)
    }
}
