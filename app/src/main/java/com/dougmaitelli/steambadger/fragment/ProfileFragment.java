package com.dougmaitelli.steambadger.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dougmaitelli.steambadger.MainActivity;
import com.dougmaitelli.steambadger.R;
import com.dougmaitelli.steambadger.database.model.Player;
import com.dougmaitelli.steambadger.util.LoaderTask;
import com.dougmaitelli.steambadger.util.Util;
import com.dougmaitelli.steambadger.view.ProfileHeaderView;
import com.dougmaitelli.steambadger.ws.Ws;

public class ProfileFragment extends Fragment {

    private Player player;

    private ProfileHeaderView header;
    private boolean fragmentLoaded = false;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!fragmentLoaded) {
            Bundle extras = getArguments();
            player = (Player) extras.getSerializable("player");

            loadPlayerData();
        }

        header = (ProfileHeaderView) getActivity().findViewById(R.id.header);
        header.setPlayer(player);

        Button badgesButton = (Button) getActivity().findViewById(R.id.badges_button);
        badgesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("player", player);
                Fragment fragment = new BadgesFragment();
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).switchContent(fragment);
            }
        });

        Button friendsButton = (Button) getActivity().findViewById(R.id.friends_button);
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("player", player);
                Fragment fragment = new FriendsFragment();
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).switchContent(fragment);
            }
        });

        fragmentLoaded = true;
    }

    private class FragmentTask extends LoaderTask<MainActivity> {

        private Player player;

        private FragmentTask(MainActivity ctx, Player player) {
            super(ctx);

            this.player = player;
        }

        @Override
        public void process() {
            try {
                if (context.getPlayer().equals(player)) {
                    Util.getPlayerData(player);
                } else {
                    Ws.getPlayerData(player);
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

        @Override
        public void onComplete() {
            ProfileFragment.this.showPlayerData(player);
        }
    }

    private void loadPlayerData() {
        new FragmentTask((MainActivity) getActivity(), player);
    }

    private void showPlayerData(Player player) {
        header.setPlayer(player);
    }

}
