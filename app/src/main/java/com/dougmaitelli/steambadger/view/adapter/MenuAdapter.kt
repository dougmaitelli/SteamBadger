package com.dougmaitelli.steambadger.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.dougmaitelli.steambadger.util.AppMenu

import java.util.ArrayList

class MenuAdapter(private val context: Context) : BaseAdapter() {
    private val adapter: MutableList<AppMenu>

    init {
        this.adapter = ArrayList()
    }

    fun add(item: AppMenu) {
        adapter.add(item)
    }

    override fun getCount(): Int {
        return adapter.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun areAllItemsSelectable(): Boolean {
        return false
    }

    override fun isEnabled(position: Int): Boolean {
        return true
    }

    override fun getItem(position: Int): AppMenu {
        return adapter[position]
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = adapter[position]

        var view: View? = convertView

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(android.R.layout.simple_list_item_1, null)
        }

        view!!.tag = item.name

        val text = view.findViewById(android.R.id.text1) as TextView
        text.text = item.description

        return view
    }
}