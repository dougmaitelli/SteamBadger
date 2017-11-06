package com.x7.steambadger.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.x7.steambadger.MainActivity;
import com.x7.steambadger.R;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.util.LoaderTask;
import com.x7.steambadger.util.Util;
import com.x7.steambadger.view.ProfileHeaderView;
import com.x7.steambadger.ws.Ws;

public class ProfileFragment extends Fragment {

    private Player player;

    private ProfileHeaderView header;

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

        Bundle extras = getArguments();
        player = (Player) extras.getSerializable("player");

        header = (ProfileHeaderView) getActivity().findViewById(R.id.header);

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

        loadPlayerData();
    }

    private void loadPlayerData() {
        new LoaderTask<MainActivity>((MainActivity) getActivity(), true) {

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
                header.setPlayer(player);
            }
        };
    }

}
