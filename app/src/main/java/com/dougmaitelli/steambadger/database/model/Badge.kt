package com.dougmaitelli.steambadger.database.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

import java.io.Serializable

@DatabaseTable
class Badge : Serializable {

    @DatabaseField(generatedId = true)
    var id: Long? = null
    @DatabaseField
    var badgeId: Int = 0
    @DatabaseField
    var appId: String? = null
    @DatabaseField
    var borderColor: Int = 0
    @DatabaseField
    var level: Int = 0
    @DatabaseField
    var text: String? = null
    @DatabaseField
    var imageUrl: String? = null
}
