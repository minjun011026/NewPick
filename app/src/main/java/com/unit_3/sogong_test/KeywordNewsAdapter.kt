package com.unit_3.sogong_test

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class KeywordNewsAdapter (val newsItems : ArrayList<KeywordNewsModel>) : RecyclerView.Adapter<KeywordNewsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordNewsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.news_rv_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: KeywordNewsAdapter.ViewHolder, position: Int) {
        holder.bindItems(newsItems[position])
    }

    override fun getItemCount(): Int {
        return newsItems.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bindItems(item: KeywordNewsModel){
            val newsArea = itemView.findViewById<TextView>(R.id.titleTextView)
            newsArea.text = item.title

            newsArea.setOnClickListener{
                val intent = Intent(itemView.context, NewsDetailActivity::class.java)
                intent.putExtra("link", item.link)
                itemView.context.startActivity(intent)
            }

        }
    }

}