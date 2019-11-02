package com.dougmaitelli.steambadger.view.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import com.dougmaitelli.steambadger.database.model.Badge
import com.dougmaitelli.steambadger.database.model.Player
import com.dougmaitelli.steambadger.view.BadgeView

import java.util.ArrayList

class BadgeAdapter(private val context: Context, private val player: Player) : RecyclerView.Adapter<BadgeAdapter.ViewHolder>() {
    private val items: MutableList<Badge>
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
        val v = BadgeView(context, player, null)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onItemClickListener)
    }

    fun add(item: Badge) {
        items.add(item)
        val position = items.indexOf(item)
        notifyItemInserted(position)
    }

    fun add(item: Badge, position: Int) {
        items.add(position, item)
        notifyItemInserted(position)
    }

    fun remove(item: Badge) {
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

        private var badgeView: BadgeView = itemView as BadgeView

        fun bind(item: Badge, listener: OnItemClickListener?) {
            badgeView.setBadge(item)
        }
    }

}