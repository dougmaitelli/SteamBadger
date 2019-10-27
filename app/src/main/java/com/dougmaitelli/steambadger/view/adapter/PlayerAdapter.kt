package com.dougmaitelli.steambadger.view.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso
import com.dougmaitelli.steambadger.R
import com.dougmaitelli.steambadger.database.model.Player

import java.util.ArrayList

class PlayerAdapter(private val context: Context) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {
    private val items: MutableList<Player>
    private var onItemClickListener: OnItemClickListener? = null

    init {
        this.items = ArrayList()
    }

    interface OnItemClickListener {
        fun onItemClick(item: Player)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.player_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onItemClickListener)
    }

    fun add(item: Player) {
        items.add(item)
        val position = items.indexOf(item)
        notifyItemInserted(position)
    }

    fun add(item: Player, position: Int) {
        items.add(position, item)
        notifyItemInserted(position)
    }

    fun remove(item: Player) {
        val position = items.indexOf(item)
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var playerAvatar: ImageView = itemView.findViewById(R.id.player_avatar)
        private var playerName: TextView = itemView.findViewById(R.id.player_name)

        fun bind(item: Player, listener: OnItemClickListener?) {
            Picasso.with(context).load(item.avatarUrl).into(playerAvatar)
            playerName.text = item.name

            itemView.setOnClickListener {
                listener!!.onItemClick(item)
            }

        }
    }

}