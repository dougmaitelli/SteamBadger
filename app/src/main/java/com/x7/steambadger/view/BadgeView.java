package com.x7.steambadger.view;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.x7.steambadger.R;
import com.x7.steambadger.database.model.Badge;
import com.x7.steambadger.ws.Util;

public class BadgeView extends LinearLayout {

    private Badge badge;

    private TextView badgeText;
    private ImageView badgeImage;

    public BadgeView(Context context) {
        super(context);
    }

    public BadgeView(Context context, Badge badge) {
        super(context);
        this.badge = badge;

        this.build();
        this.refreshBadgeData();
    }

    public BadgeView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public void build() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        LinearLayout badgeView = (LinearLayout) inflater.inflate(R.layout.badge_view, null);

        badgeText = (TextView) badgeView.findViewById(R.id.badge_text);
        badgeImage = (ImageView) badgeView.findViewById(R.id.badge_image);

        this.addView(badgeView);
    }

    public void refreshBadgeData() {
        badgeText.setText(badge.getText());
        badgeImage.setImageBitmap(Util.openLocalBadgeImage(getContext(), badge));
    }

}
