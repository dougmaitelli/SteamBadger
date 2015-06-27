package com.x7.steambadger.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.x7.steambadger.MainActivity;
import com.x7.steambadger.R;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.util.LoaderTask;
import com.x7.steambadger.util.Util;
import com.x7.steambadger.ws.Ws;

public class ProfileFragment extends Fragment {

    private Player player;

    private ImageView avatar;
    private TextView name;

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

        setHasOptionsMenu(true);

        avatar = (ImageView) getActivity().findViewById(R.id.player_photo);
        name = (TextView) getActivity().findViewById(R.id.player_name);

        Button badgesButton = (Button) getActivity().findViewById(R.id.badges_button);
        badgesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("player", player);
                Fragment fragment = new BadgesFragment();
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).setFragment(fragment);
            }
        });

        Bundle extras = getArguments();
        player = (Player) extras.getSerializable("player");

        loadPlayerData();
    }

    private void loadPlayerData() {
        new LoaderTask<MainActivity>((MainActivity) getActivity(), true) {

            @Override
            public void process() {
                try {
                    if (((MainActivity) getActivity()).getPlayer().equals(player)) {
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
                avatar.setImageBitmap(Util.byteArrayToImage(player.getAvatar()));
                name.setText(player.getName());
            }
        };
    }

}
