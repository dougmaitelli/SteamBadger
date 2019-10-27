package com.dougmaitelli.steambadger.fragment.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

import com.dougmaitelli.steambadger.MainActivity
import com.dougmaitelli.steambadger.R
import com.dougmaitelli.steambadger.util.AppMenu
import com.dougmaitelli.steambadger.view.adapter.MenuAdapter

class MenuFragment : Fragment() {

    private var adp: MenuAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.slide_menu, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val listView = activity!!.findViewById(R.id.menu_container) as ListView

        adp = MenuAdapter(activity!!)
        listView.adapter = adp

        for (menu in AppMenu.values()) {
            adp!!.add(menu)
        }

        listView.setOnItemClickListener { parent, _, position, _ ->
            val menu = parent.getItemAtPosition(position) as AppMenu

            val fragmentClass: Class<out Fragment>?

            when (menu) {
                AppMenu.LOGOFF -> {
                    (activity as MainActivity).logoff()
                    return@setOnItemClickListener
                }
                else -> fragmentClass = menu.fragmentClass
            }

            try {
                val fragment = fragmentClass!!.newInstance()

                val bundle = Bundle()
                bundle.putSerializable("player", (activity as MainActivity).player)
                fragment.arguments = bundle

                switchFragment(fragment)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        if (activity == null) {
            return
        }

        val mainActivity = activity as MainActivity
        mainActivity.switchContent(fragment)
    }

}
