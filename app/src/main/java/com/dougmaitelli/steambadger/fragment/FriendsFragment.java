package com.dougmaitelli.steambadger.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dougmaitelli.steambadger.MainActivity;
import com.dougmaitelli.steambadger.R;
import com.dougmaitelli.steambadger.database.model.Player;
import com.dougmaitelli.steambadger.util.LoaderTask;
import com.dougmaitelli.steambadger.view.adapter.PlayerAdapter;
import com.dougmaitelli.steambadger.ws.Ws;

import java.util.List;

public class FriendsFragment extends Fragment {

    private Player player;

    private PlayerAdapter adp;
    private boolean fragmentLoaded = false;

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

        if (!fragmentLoaded) {
            Bundle extras = getArguments();
            player = (Player) extras.getSerializable("player");

            adp = new PlayerAdapter(getContext());
            adp.setOnItemClickListener(new PlayerAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(Player item) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("player", item);
                    Fragment fragment = new ProfileFragment();
                    fragment.setArguments(bundle);
                    ((MainActivity) getActivity()).switchContent(fragment);
                }
            });

            loadPlayerFriends();
        }

        RecyclerView resultList = (RecyclerView) getActivity().findViewById(R.id.list_friends);
        resultList.setAdapter(adp);
        resultList.setLayoutManager(new LinearLayoutManager(getContext()));
        resultList.setItemAnimator(new DefaultItemAnimator());

        fragmentLoaded = true;
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

    private class FragmentTask extends LoaderTask<MainActivity> {

        private Player player;
        private List<Player> friends;

        private FragmentTask(MainActivity ctx, Player player) {
            super(ctx);

            this.player = player;
        }

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
            FriendsFragment.this.showPlayerFriends(friends);
        }
    }

    private void loadPlayerFriends() {
        new FragmentTask((MainActivity) getActivity(), player);
    }

    private void showPlayerFriends(List<Player> friends) {
        if (friends != null) {
            adp.clear();

            for (Player friend : friends) {
                adp.add(friend);
            }

            adp.notifyDataSetChanged();
        }
    }

}
