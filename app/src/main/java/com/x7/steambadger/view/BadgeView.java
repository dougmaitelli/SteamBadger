package com.x7.steambadger.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.x7.steambadger.R;
import com.x7.steambadger.database.model.Badge;
import com.x7.steambadger.ws.Util;

public class BadgeView extends LinearLayout {

    private Badge badge;

    private ImageView badgeImage;
    private TextView badgeText;
    private TextView badgeLevel;

    public BadgeView(Context context) {
        super(context);
    }

    public BadgeView(Context context, Badge badge) {
        super(context);
        this.badge = badge;

        this.build();
    }

    public BadgeView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public void build() {
        LinearLayout badgeView = (LinearLayout) inflate(getContext(), R.layout.badge_view, this);

        badgeImage = (ImageView) badgeView.findViewById(R.id.badge_image);
        badgeText = (TextView) badgeView.findViewById(R.id.badge_text);
        badgeLevel = (TextView) badgeView.findViewById(R.id.badge_level);

        this.refreshBadgeData();
    }

    public void refreshBadgeData() {
        badgeImage.setImageBitmap(Util.openLocalBadgeImage(getContext(), badge));
        badgeText.setText(badge.getText());

        if (badge.getAppId() != null) {
            badgeLevel.setText("Lvl.: " + badge.getLevel());
        } else {
            badgeLevel.setText(null);
        }
    }

}
