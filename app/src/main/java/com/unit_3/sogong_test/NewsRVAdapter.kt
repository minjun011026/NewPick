package com.unit_3.sogong_test

//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import org.w3c.dom.Text
//
//class NewsRVAdapter (val items : ArrayList<NewsModel>) : RecyclerView.Adapter<NewsRVAdapter.ViewHolder>(){
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsRVAdapter.ViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(R.layout.news_rv_item, parent, false)
//        return ViewHolder(v)
//    }
//
//    override fun onBindViewHolder(holder: NewsRVAdapter.ViewHolder, position: Int) {
//        holder.bindItems(items[position])
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
//
//        fun bindItems(item : NewsModel){
//
//            val imageUrl = itemView.findViewById<ImageView>(R.id.imageArea)
//
//
//            val title = itemView.findViewById<TextView>(R.id.titleArea)
//            title.text = item.title
//
//            val companyName = itemView.findViewById<TextView>(R.id.companyName)
//            companyName.text = item.companyName
//        }
//    }
//
//}

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unit_3.sogong_test.R

class NewsAdapter(private val context: Context, private val newsArticles: List<NewsModel>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        val newsImageView: ImageView = view.findViewById(R.id.newsImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = newsArticles[position]
        holder.titleTextView.text = article.title
        holder.descriptionTextView.text = article.description
        Glide.with(context)
            .load(article.imageUrl)
            .placeholder(R.drawable.favorite)
            .into(holder.newsImageView)

        holder.itemView.setOnClickListener {
            Toast.makeText(context, "기사 클릭", Toast.LENGTH_LONG).show()
////            val intent = Intent(context, NewsDetailActivity::class.java)
//            intent.putExtra("link", article.link)
//            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = newsArticles.size
}
