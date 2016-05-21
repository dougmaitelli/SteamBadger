package com.x7.steambadger.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.x7.steambadger.MainActivity;
import com.x7.steambadger.R;
import com.x7.steambadger.database.model.Player;
import com.x7.steambadger.util.LoaderTask;
import com.x7.steambadger.view.adapter.PlayerAdapter;
import com.x7.steambadger.ws.Ws;

import java.util.List;

public class FriendsFragment extends Fragment {

    private Player player;

    private ListView friendsList;
    private PlayerAdapter adp;

    public FriendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle extras = getArguments();
        player = (Player) extras.getSerializable("player");

        friendsList = (ListView) getActivity().findViewById(R.id.list_friends);
        adp = new PlayerAdapter(getActivity());

        friendsList.setAdapter(adp);

        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("player", (Player) adp.getItem(position));
                Fragment fragment = new ProfileFragment();
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).switchContent(fragment);
            }
        });

        loadPlayerFriends();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.badges_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                loadPlayerFriends();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void loadPlayerFriends() {
        new LoaderTask<MainActivity>((MainActivity) getActivity(), true) {

            List<Player> friends;

            @Override
            public void process() {
                try {
                    friends = Ws.getPlayerFriends(player);
                } catch (Exception ex) {
                    ex.printStackTrace(System.out);
                }
            }

            @Override
            public void onComplete() {
                if (friends != null) {
                    adp.clear();

                    for (Player friend : friends) {
                        adp.add(friend);
                    }

                    adp.notifyDataSetChanged();
                }
            }
        };
    }

}
