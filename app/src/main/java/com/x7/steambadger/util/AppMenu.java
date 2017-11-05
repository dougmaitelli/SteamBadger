package com.x7.steambadger.util;

import android.support.v4.app.Fragment;

import com.x7.steambadger.fragment.BadgesFragment;
import com.x7.steambadger.fragment.FriendsFragment;
import com.x7.steambadger.fragment.ProfileFragment;

public enum AppMenu {

    PROFILE("Profile", ProfileFragment.class),
    BADGES("Badges", BadgesFragment.class),
    FRIENDS("Friends", FriendsFragment.class),
    LOGOFF("LogOff", null);

    private String description;
    private Class<? extends Fragment> fragmentClass;

    AppMenu(String description, Class<? extends Fragment> fragmentClass){
        this.description = description;
        this.fragmentClass = fragmentClass;
    }

    public String getDescription() {
        return description;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }

    @Override
    public String toString() {
        return description;
    }
}
