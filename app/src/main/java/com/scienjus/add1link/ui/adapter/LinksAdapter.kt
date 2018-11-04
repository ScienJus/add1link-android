package com.scienjus.add1link.ui.adapter

import FeedQuery
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.scienjus.add1link.R
import java.text.SimpleDateFormat
import java.util.*

class LinksAdapter(val context: Context, var links: List<FeedQuery.Link>, var linkClickListener: ((View, FeedQuery.Link) -> Unit)? = null) :
        RecyclerView.Adapter<LinksAdapter.ViewHolder>(), View.OnClickListener {

    override fun onClick(v: View?) {
        linkClickListener?.invoke(v!!, v.tag as FeedQuery.Link)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById(R.id.link_title)
        val createDate: TextView = view.findViewById(R.id.link_create_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_link, parent, false)
        val viewHolder = ViewHolder(view)

        view.setOnClickListener(this)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        links[position].apply {
            holder.title.text = title()
            holder.createDate.text = SimpleDateFormat("yyyy-MM-dd").format(Date(createdAt()))
            holder.itemView.tag = this
        }
    }

    override fun getItemCount() = links.size
}