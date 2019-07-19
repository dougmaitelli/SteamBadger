package com.dougmaitelli.steambadger.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.dougmaitelli.steambadger.MainActivity
import com.dougmaitelli.steambadger.R
import com.dougmaitelli.steambadger.database.model.Player
import com.dougmaitelli.steambadger.util.LoaderTask
import com.dougmaitelli.steambadger.view.adapter.PlayerAdapter
import com.dougmaitelli.steambadger.ws.Ws

class FriendsFragment : Fragment() {

    private var player: Player? = null

    private var adp: PlayerAdapter? = null
    private var fragmentLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        if (!fragmentLoaded) {
            player = arguments!!.getSerializable("player") as Player

            adp = PlayerAdapter(context!!)
            adp!!.setOnItemClickListener(object : PlayerAdapter.OnItemClickListener{
                override fun onItemClick(item: Player) {
                    val bundle = Bundle()
                    bundle.putSerializable("player", item)
                    val fragment = ProfileFragment()
                    fragment.arguments = bundle
                    (activity as MainActivity).switchContent(fragment)
                }
            })

            loadPlayerFriends()
        }

        val resultList = activity!!.findViewById(R.id.list_friends) as RecyclerView
        resultList.adapter = adp
        resultList.layoutManager = LinearLayoutManager(context)
        resultList.itemAnimator = DefaultItemAnimator()

        fragmentLoaded = true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.badges_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> loadPlayerFriends()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private inner class FragmentTask constructor(ctx: MainActivity, private val player: Player) : LoaderTask<MainActivity>(ctx) {
        private var friends: List<Player>? = null

        override fun process() {
            try {
                friends = Ws.getPlayerFriends(player)
            } catch (ex: Exception) {
                ex.printStackTrace(System.out)
            }

        }

        override fun onComplete() {
            this@FriendsFragment.showPlayerFriends(friends)
        }
    }

    private fun loadPlayerFriends() {
        FragmentTask(activity as MainActivity, player!!)
    }

    private fun showPlayerFriends(friends: List<Player>?) {
        if (friends != null) {
            adp!!.clear()

            for (friend in friends) {
                adp!!.add(friend)
            }

            adp!!.notifyDataSetChanged()
        }
    }

}
