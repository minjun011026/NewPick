package com.unit_3.sogong_test

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HotNewsRVAdapter(private var hotNewsList: List<HotNewsModel>) :
    RecyclerView.Adapter<HotNewsRVAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleArea)
        val imageView: ImageView = itemView.findViewById(R.id.imageArea)
        val moreLayout: ConstraintLayout = itemView.findViewById(R.id.moreLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hotnews_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = hotNewsList[position]
        holder.titleTextView.text = currentItem.title
        Glide.with(holder.itemView.context).load(currentItem.imageUrl).into(holder.imageView)

        holder.moreLayout.setOnClickListener {
            val intent = Intent(holder.itemView.context, WebViewActivity::class.java).apply {
                putExtra("link", hotNewsList[position].url)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return hotNewsList.size
    }

    fun updateNewsList(newHotNewsList: List<HotNewsModel>) {
        hotNewsList = newHotNewsList
        notifyDataSetChanged()
    }
}
