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
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsRVAdapter(private val context: Context, private val newsArticles: List<NewsModel>) :
    RecyclerView.Adapter<NewsRVAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        val newsImageView: ImageView = view.findViewById(R.id.newsImageView)
        val moreVertBtn: ImageButton = view.findViewById(R.id.moreVertBtn)
        val buttonLayout: LinearLayout = view.findViewById(R.id.buttonLayout)
        val button1: Button = view.findViewById(R.id.button1)
        val button2: Button = view.findViewById(R.id.button2)
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
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("link", article.link)
            context.startActivity(intent)
        }

        holder.moreVertBtn.setOnClickListener {
            if (holder.buttonLayout.visibility == View.GONE) {
                holder.buttonLayout.visibility = View.VISIBLE
            } else {
                holder.buttonLayout.visibility = View.GONE
            }
        }

        holder.button1.setOnClickListener {
            Toast.makeText(context, "삭제 버튼 클릭", Toast.LENGTH_SHORT).show()
            // 삭제 버튼 클릭 시 동작 추가
        }

        holder.button2.setOnClickListener {
            Toast.makeText(context, "알림 버튼 클릭", Toast.LENGTH_SHORT).show()
            // 알림 버튼 클릭 시 동작 추가
        }

        //더보기 버튼
        holder.moreVertBtn.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.moreVertBtn)
            popupMenu.menuInflater.inflate(R.menu.news_popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_delete -> {
                        Toast.makeText(context, "삭제 클릭", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.menu_notify -> {
                        Toast.makeText(context, "알림 클릭", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int = newsArticles.size
}
