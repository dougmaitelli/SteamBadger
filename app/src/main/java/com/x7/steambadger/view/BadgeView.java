package com.x7.steambadger.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.squareup.picasso.Picasso;
import com.x7.steambadger.R;
import com.x7.steambadger.database.DbOpenHelper;
import com.x7.steambadger.database.model.Badge;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.util.LoaderTask;
import com.x7.steambadger.ws.Ws;

public class BadgeView extends LinearLayout {

    private Player player;
    private Badge badge;

    private ImageView badgeImage;
    private TextView badgeText;
    private TextView badgeLevel;

    public BadgeView(Context context) {
        super(context);
    }

    public BadgeView(Context context, Player player, Badge badge) {
        super(context);
        this.player = player;
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
        if (badge.getImageUrl() == null) {
            badgeImage.setImageBitmap(null);
            badgeText.setText(null);

            new LoaderTask<Context>(getContext(), false) {

                @Override
                public void process() {
                    try {
                        Dao<Badge, Long> badgeDao = DaoManager.createDao(DbOpenHelper.getCon(), Badge.class);

                        Ws.loadBadgeData(player, badge);
                        badgeDao.update(badge);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onComplete() {
                    Picasso.with(getContext()).load(badge.getImageUrl()).into(badgeImage);
                    badgeText.setText(badge.getText());
                }
            };
        } else {
            Picasso.with(getContext()).load(badge.getImageUrl()).into(badgeImage);
            badgeText.setText(badge.getText());
        }

        if (badge.getAppId() != null) {
            badgeLevel.setText("Lvl.: " + badge.getLevel());
        } else {
            badgeLevel.setText(null);
        }
    }

}
