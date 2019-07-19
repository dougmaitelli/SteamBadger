package com.dougmaitelli.steambadger.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridView

import com.dougmaitelli.steambadger.MainActivity
import com.dougmaitelli.steambadger.R
import com.dougmaitelli.steambadger.database.model.Player
import com.dougmaitelli.steambadger.database.model.PlayerBadge
import com.dougmaitelli.steambadger.util.LoaderTask
import com.dougmaitelli.steambadger.util.Util
import com.dougmaitelli.steambadger.view.ProfileHeaderView
import com.dougmaitelli.steambadger.view.adapter.BadgeAdapter
import com.dougmaitelli.steambadger.ws.Ws

class BadgesFragment : Fragment() {

    private var player: Player? = null

    private var header: ProfileHeaderView? = null
    private var adp: BadgeAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_badges, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        player = arguments!!.getSerializable("player") as Player

        header = activity!!.findViewById(R.id.header) as ProfileHeaderView
        header!!.setPlayer(player!!)

        adp = BadgeAdapter(activity!!, player!!)

        val badgesLayout = activity!!.findViewById(R.id.badges_layout) as RecyclerView
        badgesLayout.adapter = adp
        badgesLayout.itemAnimator = DefaultItemAnimator()

        if (player!!.isBadgesLoaded) {
            showPlayerBadges()
        } else {
            loadPlayerBadges()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.badges_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> loadPlayerBadges()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private inner class FragmentTask constructor(ctx: MainActivity, private val player: Player) : LoaderTask<MainActivity>(ctx) {

        override fun process() {
            try {
                if (context.player == player) {
                    Util.getPlayerBadges(player)
                } else {
                    Ws.getPlayerBadges(player)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }

        override fun onComplete() {
            this@BadgesFragment.showPlayerBadges()
        }
    }

    private fun loadPlayerBadges() {
        FragmentTask(activity as MainActivity, player!!)
    }

    private fun showPlayerBadges() {
        header!!.refreshData()

        adp!!.clear()

        for (playerBadge in player!!.playerBadges) {
            adp!!.add(playerBadge.badge!!)
        }

        adp!!.notifyDataSetChanged()
    }

}
