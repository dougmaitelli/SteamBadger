package com.dougmaitelli.steambadger.fragment.menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dougmaitelli.steambadger.MainActivity;
import com.dougmaitelli.steambadger.R;
import com.dougmaitelli.steambadger.util.AppMenu;
import com.dougmaitelli.steambadger.view.adapter.MenuAdapter;

public class MenuFragment extends Fragment {

    private MenuAdapter adp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.slide_menu, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = (ListView) getActivity().findViewById(R.id.menu_container);

        adp = new MenuAdapter(getActivity());
        listView.setAdapter(adp);

        for (AppMenu menu : AppMenu.values()) {
            adp.add(menu);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppMenu menu = (AppMenu) parent.getItemAtPosition(position);

                Class<? extends Fragment> fragmentClass;

                switch (menu) {
                    case LOGOFF:
                        ((MainActivity) getActivity()).logoff();
                        return;
                    default:
                        fragmentClass = menu.getFragmentClass();
                }

                try {
                    Fragment fragment = fragmentClass.newInstance();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("player", ((MainActivity) getActivity()).getPlayer());
                    fragment.setArguments(bundle);

                    switchFragment(fragment);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void switchFragment(Fragment fragment) {
        if (getActivity() == null) {
            return;
        }

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.switchContent(fragment);
    }

}
