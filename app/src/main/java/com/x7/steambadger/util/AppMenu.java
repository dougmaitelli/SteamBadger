package com.x7.steambadger.util;

import android.support.v4.app.Fragment;

import com.x7.steambadger.fragment.BadgesFragment;
import com.x7.steambadger.fragment.FriendsFragment;
import com.x7.steambadger.fragment.ProfileFragment;

public enum AppMenu {

    PROFILE("Profile", ProfileFragment.class),
    BADGES("Badges", BadgesFragment.class),
    FRIENDS("Friends", FriendsFragment.class);

    private String description;
    private Class<? extends Fragment> fragment;

    AppMenu(String description, Class<? extends Fragment> fragment){
        this.description = description;
        this.fragment = fragment;
    }

    public String getDescription() {
        return description;
    }

    public Class<? extends Fragment> getFragment() {
        return fragment;
    }

    @Override
    public String toString() {
        return description;
    }
}
