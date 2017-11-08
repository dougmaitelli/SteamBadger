package com.dougmaitelli.steambadger.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.squareup.picasso.Picasso;
import com.dougmaitelli.steambadger.R;
import com.dougmaitelli.steambadger.database.DbOpenHelper;
import com.dougmaitelli.steambadger.database.model.Badge;
import com.dougmaitelli.steambadger.database.model.Player;
import com.dougmaitelli.steambadger.util.LoaderTask;
import com.dougmaitelli.steambadger.ws.Ws;

public class BadgeView extends LinearLayout {

    private Player player;
    private Badge badge;

    private ImageView badgeImage;
    private TextView badgeText;
    private TextView badgeLevel;

    public BadgeView(Context context) {
        super(context);

        this.build();
    }

    public BadgeView(Context context, Player player, Badge badge) {
        super(context);
        this.player = player;
        this.badge = badge;

        this.build();
        this.refreshData();
    }

    public BadgeView(Context context, AttributeSet attrs){
        super(context, attrs);

        this.build();
    }

    public void setBadge(Badge badge) {
        this.badge = badge;

        this.refreshData();
    }

    public void build() {
        LinearLayout badgeView = (LinearLayout) inflate(getContext(), R.layout.badge_view, this);

        badgeImage = (ImageView) badgeView.findViewById(R.id.badge_image);
        badgeText = (TextView) badgeView.findViewById(R.id.badge_text);
        badgeLevel = (TextView) badgeView.findViewById(R.id.badge_level);
    }

    public void refreshData() {
        if (badge == null) {
            return;
        }

        if (badge.getImageUrl() == null) {
            badgeImage.setImageBitmap(null);
            badgeText.setText(null);

            final Handler imageHandler = new Handler();

            new Thread(new Runnable() {

                public void run() {
                    try {
                        Dao<Badge, Long> badgeDao = DaoManager.createDao(DbOpenHelper.getCon(), Badge.class);

                        Ws.loadBadgeData(player, badge);
                        badgeDao.update(badge);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    imageHandler.post(new Runnable() {

                        public void run() {
                            Picasso.with(getContext()).load(badge.getImageUrl()).into(badgeImage);
                            badgeText.setText(badge.getText());
                        }
                    });

                }
            }).start();
        } else {
            Picasso.with(getContext()).load(badge.getImageUrl()).into(badgeImage);
            badgeText.setText(badge.getText());
        }

        if (badge.getAppId() != null) {
            badgeLevel.setText(getResources().getString(R.string.lvl, badge.getLevel()));
        } else {
            badgeLevel.setText(null);
        }
    }

}
