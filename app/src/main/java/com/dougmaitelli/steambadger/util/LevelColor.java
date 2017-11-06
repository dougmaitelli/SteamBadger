package com.dougmaitelli.steambadger.util;

import android.graphics.Color;

public enum LevelColor {

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

    private int color;

    LevelColor(String colorStr) {
        this.color = Color.parseColor(colorStr);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static LevelColor getLevelColor(int level) {
        level = level % 100;

        level -= level % 10;

        return valueOf("LVL_" + level);
    }
}
