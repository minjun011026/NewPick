package com.unit_3.sogong_test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class FeedRVAdapter (private val items : MutableList<FeedModel>) : RecyclerView.Adapter<FeedRVAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedRVAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.feed_rv_item, parent, false)
        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: FeedRVAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bindItems(item : FeedModel){

            val name = itemView.findViewById<TextView>(R.id.nameTextView)
            val title = itemView.findViewById<TextView>(R.id.titleTextView)
            val time = itemView.findViewById<TextView>(R.id.timeTextView)
            val content = itemView.findViewById<TextView>(R.id.contentTextView)

            name.text = item.uid
            title.text = item.title
            time.text = item.time
            content.text = item.content

        }
    }
}