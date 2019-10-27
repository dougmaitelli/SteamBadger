package com.dougmaitelli.steambadger.view

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.j256.ormlite.dao.DaoManager
import com.squareup.picasso.Picasso
import com.dougmaitelli.steambadger.R
import com.dougmaitelli.steambadger.database.DatabaseHelper
import com.dougmaitelli.steambadger.database.model.Badge
import com.dougmaitelli.steambadger.database.model.Player
import com.dougmaitelli.steambadger.ws.Ws

class BadgeView : LinearLayout {

    private var player: Player? = null
    private var badge: Badge? = null

    private var badgeImage: ImageView? = null
    private var badgeText: TextView? = null
    private var badgeLevel: TextView? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    constructor(context: Context, player: Player, badge: Badge?) : this(context) {
        this.player = player
        this.badge = badge

        this.build()
        this.refreshData()
    }

    fun setBadge(badge: Badge) {
        this.badge = badge

        this.refreshData()
    }

    private fun build() {
        val badgeView = inflate(context, R.layout.badge_view, this) as LinearLayout

        badgeImage = badgeView.findViewById(R.id.badge_image)
        badgeText = badgeView.findViewById(R.id.badge_text)
        badgeLevel = badgeView.findViewById(R.id.badge_level)
    }

    private fun refreshData() {
        if (badge == null) {
            return
        }

        if (badge!!.imageUrl == null) {
            badgeImage!!.setImageBitmap(null)
            badgeText!!.text = null

            val imageHandler = Handler()

            Thread {
                try {
                    val badgeDao = DaoManager.createDao(DatabaseHelper.connectionSource, Badge::class.java)

                    Ws.loadBadgeData(player!!, badge!!)
                    badgeDao.update(badge)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                imageHandler.post {
                    Picasso.with(context).load(badge!!.imageUrl).into(badgeImage)
                    badgeText!!.text = badge!!.text
                }
            }.start()
        } else {
            Picasso.with(context).load(badge!!.imageUrl).into(badgeImage)
            badgeText!!.text = badge!!.text
        }

        if (badge!!.appId != null) {
            badgeLevel!!.text = resources.getString(R.string.lvl, badge!!.level)
        } else {
            badgeLevel!!.text = null
        }
    }

}
