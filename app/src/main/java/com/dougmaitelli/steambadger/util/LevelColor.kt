package com.dougmaitelli.steambadger.util

import android.graphics.Color

enum class LevelColor private constructor(colorStr: String) {

    LVL_0("#FF9B9B9B"),
    LVL_10("#FFC02942"),
    LVL_20("#FFD95B43"),
    LVL_30("#FFfECC23"),
    LVL_40("#FF467A3C"),
    LVL_50("#FF4E8DDB"),
    LVL_60("#FF7652C9"),
    LVL_70("#FFC252C9"),
    LVL_80("#FF542437"),
    LVL_90("#FF997C52");

    var color: Int = 0

    init {
        this.color = Color.parseColor(colorStr)
    }

    companion object {

        fun getLevelColor(level: Int): LevelColor {
            var levelRound = level
            levelRound %= 100

            levelRound -= levelRound % 10

            return valueOf("LVL_$levelRound")
        }
    }
}
