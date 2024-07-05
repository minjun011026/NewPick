package com.unit_3.sogong_test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class NewsRVAdapter (val items : ArrayList<NewsModel>) : RecyclerView.Adapter<NewsRVAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsRVAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.news_rv_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: NewsRVAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bindItems(item : NewsModel){

            val imageUrl = itemView.findViewById<ImageView>(R.id.imageArea)


            val title = itemView.findViewById<TextView>(R.id.titleArea)
            title.text = item.title

            val companyName = itemView.findViewById<TextView>(R.id.companyName)
            companyName.text = item.companyName
        }
    }

}