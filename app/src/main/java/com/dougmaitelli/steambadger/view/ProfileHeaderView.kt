package com.dougmaitelli.steambadger.view

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.squareup.picasso.Picasso
import com.dougmaitelli.steambadger.R
import com.dougmaitelli.steambadger.database.model.Player
import com.dougmaitelli.steambadger.util.LevelColor
import android.graphics.PorterDuff
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.os.Build

class ProfileHeaderView : LinearLayout {

    private var player: Player? = null

    private var avatar: ImageView? = null
    private var name: TextView? = null
    private var levelProgress: ProgressBar? = null
    private var playerExp: TextView? = null
    private var level: TextView? = null

    constructor(context: Context) : super(context) {
        this.build()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.build()
    }

    constructor(context: Context, player: Player) : super(context) {
        this.player = player

        this.build()
        this.refreshData()
    }

    fun setPlayer(player: Player) {
        this.player = player

        this.refreshData()
    }

    private fun build() {
        val badgeView = inflate(context, R.layout.profileheader_view, this)

        avatar = badgeView.findViewById(R.id.player_photo)
        name = badgeView.findViewById(R.id.player_name)
        levelProgress = badgeView.findViewById(R.id.level_progress)
        playerExp = badgeView.findViewById(R.id.player_exp)
        level = badgeView.findViewById(R.id.level)
    }

    fun refreshData() {
        val hideInfo = if (player == null || player!!.playerLevel == 0) INVISIBLE else VISIBLE

        levelProgress!!.visibility = hideInfo
        playerExp!!.visibility = hideInfo
        level!!.visibility = hideInfo

        if (player != null) {
            val progressBarHandler = Handler()

            Picasso.with(context).load(player!!.avatarUrl).into(avatar)
            name!!.text = player!!.name

            val mRunnable = Runnable {
                val xpOnCurrentLevel = (player!!.playerXp - player!!.playerXpNeededCurrentLevel)

                levelProgress!!.progress = (xpOnCurrentLevel.toDouble() / (xpOnCurrentLevel + player!!.playerXpNeededToLevelUp).toDouble() * 100).toInt()
            }

            progressBarHandler.post(mRunnable)

            playerExp!!.text = resources.getString(R.string.xp, player!!.playerXp)
            level!!.text = player!!.playerLevel.toString()
            level!!.background.level = player!!.playerLevel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                level!!.background.current.colorFilter = BlendModeColorFilter(LevelColor.getLevelColor(player!!.playerLevel).color, BlendMode.MULTIPLY)
            } else {
                @Suppress("DEPRECATION")
                level!!.background.current.setColorFilter(LevelColor.getLevelColor(player!!.playerLevel).color, PorterDuff.Mode.MULTIPLY)
            }
        } else {
            avatar!!.setImageBitmap(null)
            name!!.text = ""

            levelProgress!!.progress = 0
            playerExp!!.text = ""
            level!!.text = 0.toString()
            level!!.background.level = 0
        }
    }

}
