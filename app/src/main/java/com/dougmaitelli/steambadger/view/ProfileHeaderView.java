package com.dougmaitelli.steambadger.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.dougmaitelli.steambadger.R;
import com.dougmaitelli.steambadger.database.model.Player;
import com.dougmaitelli.steambadger.util.LevelColor;

public class ProfileHeaderView extends LinearLayout {

    private Player player;

    private ImageView avatar;
    private TextView name;
    private ProgressBar levelProgress;
    private TextView playerExp;
    private TextView level;

    public ProfileHeaderView(Context context) {
        super(context);

        this.build();
    }

    public ProfileHeaderView(Context context, Player player) {
        super(context);
        this.player = player;

        this.build();
        this.refreshData();
    }

    public ProfileHeaderView(Context context, AttributeSet attrs){
        super(context, attrs);

        this.build();
    }

    public void setPlayer(Player player) {
        this.player = player;

        this.refreshData();
    }

    public void build() {
        LinearLayout badgeView = (LinearLayout) inflate(getContext(), R.layout.profileheader_view, this);

        avatar = (ImageView) badgeView.findViewById(R.id.player_photo);
        name = (TextView) badgeView.findViewById(R.id.player_name);
        levelProgress = (ProgressBar) badgeView.findViewById(R.id.level_progress);
        playerExp = (TextView) badgeView.findViewById(R.id.player_exp);
        level = (TextView) badgeView.findViewById(R.id.level);
    }

    public void refreshData() {
        int hideInfo = player == null || player.getPlayerLevel() == 0 ? INVISIBLE : VISIBLE;

        levelProgress.setVisibility(hideInfo);
        playerExp.setVisibility(hideInfo);
        level.setVisibility(hideInfo);

        if (player != null) {
            Handler progressBarHandler = new Handler();

            Picasso.with(getContext()).load(player.getAvatarUrl()).into(avatar);
            name.setText(player.getName());

            progressBarHandler.post(new Runnable() {

                public void run() {
                    levelProgress.setProgress((int) ((double) (player.getPlayerXp() - player.getPlayerXpNeededCurrentLevel()) / (double) (player.getPlayerXp() - player.getPlayerXpNeededCurrentLevel() + player.getPlayerXpNeededToLevelUp()) * 100));
                }
            });

            playerExp.setText("XP: " + player.getPlayerXp());
            level.setText(String.valueOf(player.getPlayerLevel()));
            level.getBackground().setLevel(player.getPlayerLevel());
            ((GradientDrawable) level.getBackground().getCurrent()).setColor(LevelColor.getLevelColor(player.getPlayerLevel()).getColor());
        } else {
            avatar.setImageBitmap(null);
            name.setText("");

            levelProgress.setProgress(0);
            playerExp.setText("");
            level.setText(String.valueOf(0));
            level.getBackground().setLevel(0);
        }
    }

}
