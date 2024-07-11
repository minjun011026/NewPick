package com.unit_3.sogong_test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import fragments.SummaryDialogFragment

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

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: KeywordNewsModel) {
            val newsArea = itemView.findViewById<TextView>(R.id.titleTextView)
            newsArea.text = item.title

            newsArea.setOnClickListener {
                val activity = itemView.context as? FragmentActivity
                activity?.let {
                    val dialogFragment = SummaryDialogFragment.newInstance(item.link)
                    dialogFragment.show(it.supportFragmentManager, "SummaryDialogFragment")
                }

//            newsArea.setOnClickListener {
////                val intent = Intent(itemView.context, WebViewActivity::class.java)
//                val intent = Intent(itemView.context, KeywordNewsActivity::class.java)
//                intent.putExtra("link", item.link)
//                itemView.context.startActivity(intent)
//            }

            }
        }
    }
}