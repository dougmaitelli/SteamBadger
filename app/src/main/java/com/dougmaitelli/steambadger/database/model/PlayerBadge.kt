package com.dougmaitelli.steambadger.database.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.io.Serializable

@DatabaseTable
data class PlayerBadge(

    @DatabaseField(generatedId = true)
    var id: Long? = null,
    @DatabaseField(foreign = true)
    var player: Player? = null,
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    var badge: Badge? = null
) : Serializable