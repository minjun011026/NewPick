package com.unit_3.sogong_test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KeywordRVAdapter (val items : ArrayList<KeywordModel>) : RecyclerView.Adapter<KeywordRVAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordRVAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.keyword_rv_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: KeywordRVAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bindItems(item: KeywordModel){

            val keywordArea = itemView.findViewById<TextView>(R.id.keywordArea)
            keywordArea.text = item.keyword


        }
    }

}