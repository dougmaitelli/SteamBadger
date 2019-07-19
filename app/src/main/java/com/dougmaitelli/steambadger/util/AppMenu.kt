package com.dougmaitelli.steambadger.util

import androidx.fragment.app.Fragment

import com.dougmaitelli.steambadger.fragment.BadgesFragment
import com.dougmaitelli.steambadger.fragment.FriendsFragment
import com.dougmaitelli.steambadger.fragment.ProfileFragment

enum class AppMenu constructor(val description: String, val fragmentClass: Class<out Fragment>?) {

    PROFILE("Profile", ProfileFragment::class.java),
    BADGES("Badges", BadgesFragment::class.java),
    FRIENDS("Friends", FriendsFragment::class.java),
    LOGOFF("Log Off", null);

    override fun toString(): String {
        return description
    }
}
