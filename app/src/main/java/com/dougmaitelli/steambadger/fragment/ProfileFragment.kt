package com.dougmaitelli.steambadger.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.dougmaitelli.steambadger.MainActivity
import com.dougmaitelli.steambadger.R
import com.dougmaitelli.steambadger.database.model.Player
import com.dougmaitelli.steambadger.util.LoaderTask
import com.dougmaitelli.steambadger.util.Util
import com.dougmaitelli.steambadger.view.ProfileHeaderView
import com.dougmaitelli.steambadger.ws.Ws

class ProfileFragment : Fragment() {

    private var player: Player? = null

    private var header: ProfileHeaderView? = null
    private var fragmentLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (!fragmentLoaded) {
            player = requireArguments().getSerializable("player") as Player?

            loadPlayerData()
        }

        header = requireActivity().findViewById(R.id.header) as ProfileHeaderView
        header!!.setPlayer(player!!)

        val badgesButton = requireActivity().findViewById(R.id.badges_button) as Button
        badgesButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("player", player)
            val fragment = BadgesFragment()
            fragment.arguments = bundle
            (activity as MainActivity).switchContent(fragment)
        }

        val friendsButton = requireActivity().findViewById(R.id.friends_button) as Button
        friendsButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("player", player)
            val fragment = FriendsFragment()
            fragment.arguments = bundle
            (activity as MainActivity).switchContent(fragment)
        }

        fragmentLoaded = true
    }

    private class FragmentTask constructor(ctx: MainActivity, private val fragment: ProfileFragment, private val player: Player) : LoaderTask<MainActivity>(ctx) {

        override fun process() {
            try {
                if (context.player == player) {
                    Util.getPlayerData(player)
                } else {
                    Ws.getPlayerData(player)
                }
            } catch (ex: Exception) {
                println(ex)
            }

        }

        override fun onComplete() {
            fragment.showPlayerData(player)
        }
    }

    private fun loadPlayerData() {
        FragmentTask(activity as MainActivity, this, player!!)
    }

    private fun showPlayerData(player: Player) {
        header!!.setPlayer(player)
    }

}
