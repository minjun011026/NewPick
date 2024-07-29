//package com.unit_3.sogong_test
//
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.text.Spannable
//import android.text.SpannableString
//import android.text.style.StyleSpan
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.PopupMenu
//import android.widget.TextView
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.core.text.toSpannable
//import androidx.fragment.app.FragmentActivity
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import fragments.SummaryDialogFragment
//
//class NewsRVAdapter(private val context: Context, private val newsArticles: List<NewsModel>) :
//    RecyclerView.Adapter<NewsRVAdapter.ViewHolder>() {
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
//        val newsImageView: ImageView = view.findViewById(R.id.newsImageView)
//        val moreVertBtn: ImageButton = view.findViewById(R.id.moreVertBtn)
//        val buttonLayout: LinearLayout = view.findViewById(R.id.buttonLayout)
//        val button1: Button = view.findViewById(R.id.button1)
//        val button2: Button = view.findViewById(R.id.button2)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_rv_item, parent, false)
//        return ViewHolder(view)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val article = newsArticles[position]
//        val keyword = article.keyword
//        val title = article.title
//        val newsImageView = holder.newsImageView
//
//        // Create a SpannableString for the title
//        val spannableTitle = SpannableString(title)
//        val keywordStartIndex = title.indexOf(keyword, ignoreCase = true)
//
//        if (keywordStartIndex != -1) {
//            val keywordEndIndex = keywordStartIndex + keyword.length
//            spannableTitle.setSpan(
//                StyleSpan(android.graphics.Typeface.BOLD),
//                keywordStartIndex,
//                keywordEndIndex,
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//        }
//
//        holder.titleTextView.text = spannableTitle
//
//        Glide.with(context)
//            .load(article.imageUrl)
//            .placeholder(R.drawable.no_image)
//            .into(holder.newsImageView)
//
//        //둥글게
//        newsImageView.clipToOutline = true
//
//        holder.itemView.setOnClickListener {
//            Toast.makeText(context, "기사 클릭", Toast.LENGTH_LONG).show()
//            val activity = context as? FragmentActivity
//            activity?.let {
//                val dialogFragment = SummaryDialogFragment.newInstance(article.link)
//                dialogFragment.show(it.supportFragmentManager, "SummaryDialogFragment")
//            }
//        }
//
//        holder.moreVertBtn.setOnClickListener {
//            if (holder.buttonLayout.visibility == View.GONE) {
//                holder.buttonLayout.visibility = View.VISIBLE
//            } else {
//                holder.buttonLayout.visibility = View.GONE
//            }
//        }
//
//        holder.button1.setOnClickListener {
//            Toast.makeText(context, "삭제 버튼 클릭", Toast.LENGTH_SHORT).show()
//            // 삭제 버튼 클릭 시 동작 추가
//        }
//
//        holder.button2.setOnClickListener {
//            Toast.makeText(context, "알림 버튼 클릭", Toast.LENGTH_SHORT).show()
//            // 알림 버튼 클릭 시 동작 추가
//        }
//
//        holder.moreVertBtn.setOnClickListener {
//            val popupMenu = PopupMenu(context, holder.moreVertBtn)
//            popupMenu.menuInflater.inflate(R.menu.news_popup, popupMenu.menu)
//            popupMenu.setOnMenuItemClickListener { item ->
//                when (item.itemId) {
//                    R.id.menu_bookmark -> {
//                        Toast.makeText(context, "북마크 클릭", Toast.LENGTH_SHORT).show()
//
//                        val dbHelper = DatabaseHelper()
//                        val newsTitle = article.title
//                        val newsUrl = article.link
//                        val imageUrl = article.imageUrl
//                        val news = BookmarkedNewsModel(newsTitle, newsUrl, imageUrl)
//
//                        dbHelper.addBookmarkedNews(news) { isSuccess ->
//                            if (isSuccess) {
//                                Toast.makeText(context, "기사가 북마크되었습니다.", Toast.LENGTH_SHORT).show()
//                            } else {
//                                Toast.makeText(context, "북마크에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//
//                        true
//                    }
//
//                    R.id.menu_share_link -> {
//                        Toast.makeText(context, "링크 공유 클릭", Toast.LENGTH_SHORT).show()
//
//                        val intent = Intent(Intent.ACTION_SEND)
//                        intent.type = "text/plain"
//                        val sharedUrl = article.link
//                        val content = "친구가 링크를 공유했어요!\n어떤 링크인지 들어가서 확인해볼까요?"
//                        intent.putExtra(Intent.EXTRA_TEXT, "$content\n\n$sharedUrl")
//                        val chooserTitle = "친구에게 공유하기"
//                        context.startActivity(Intent.createChooser(intent, chooserTitle))
//
//                        true
//                    }
//
//                    R.id.menu_posting -> {
//                        Toast.makeText(context, "글 쓰기 클릭", Toast.LENGTH_SHORT).show()
//
//                        val intent = Intent(context, FeedWriteActivity::class.java).apply {
//                            putExtra("article_link", article.link)
//                            putExtra("article_title", article.title)
//                            putExtra("article_imageUrl", article.imageUrl)
//                        }
//                        context.startActivity(intent)
//
//                        true
//                    }
//
//                    else -> false
//                }
//            }
//
//            popupMenu.setForceShowIcon(true)
//            popupMenu.show()
//        }
//    }
//
//    override fun getItemCount(): Int = newsArticles.size
//}

package com.unit_3.sogong_test

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fragments.SummaryDialogFragment

class NewsRVAdapter(private val context: Context, private val newsArticles: List<NewsModel>) :
    RecyclerView.Adapter<NewsRVAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = newsArticles[position]
        val keyword = article.keyword
        val title = article.title
        val newsImageView = holder.newsImageView

        // Create a SpannableString for the title
        val spannableTitle = SpannableString(title)
        val keywordStartIndex = title.indexOf(keyword, ignoreCase = true)

        if (keywordStartIndex != -1) {
            val keywordEndIndex = keywordStartIndex + keyword.length
            spannableTitle.setSpan(
                StyleSpan(android.graphics.Typeface.BOLD),
                keywordStartIndex,
                keywordEndIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        holder.titleTextView.text = spannableTitle

        Glide.with(context)
            .load(article.imageUrl)
            .placeholder(R.drawable.no_image)
            .into(holder.newsImageView)

        // 둥글게
        newsImageView.clipToOutline = true

        holder.itemView.setOnClickListener {
            Toast.makeText(context, "기사 클릭", Toast.LENGTH_LONG).show()
            val activity = context as? FragmentActivity
            activity?.let {
                val dialogFragment = SummaryDialogFragment.newInstance(article.link)
                dialogFragment.show(it.supportFragmentManager, "SummaryDialogFragment")
            }
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

        holder.moreVertBtn.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.moreVertBtn)
            popupMenu.menuInflater.inflate(R.menu.news_popup, popupMenu.menu)

            for (i in 0 until popupMenu.menu.size()) {
                val menuItem = popupMenu.menu.getItem(i)
                applyCustomFontAndPadding(menuItem)
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_bookmark -> {
                        Toast.makeText(context, "북마크 클릭", Toast.LENGTH_SHORT).show()
                        val dbHelper = DatabaseHelper()
                        val newsTitle = article.title
                        val newsUrl = article.link
                        val imageUrl = article.imageUrl
                        val news = BookmarkedNewsModel(newsTitle, newsUrl, imageUrl)

                        dbHelper.addBookmarkedNews(news) { isSuccess ->
                            if (isSuccess) {
                                Toast.makeText(context, "기사가 북마크되었습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "북마크에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        true
                    }

                    R.id.menu_share_link -> {
                        Toast.makeText(context, "링크 공유 클릭", Toast.LENGTH_SHORT).show()

                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        val sharedUrl = article.link
                        val content = "친구가 링크를 공유했어요!\n어떤 링크인지 들어가서 확인해볼까요?"
                        intent.putExtra(Intent.EXTRA_TEXT, "$content\n\n$sharedUrl")
                        val chooserTitle = "친구에게 공유하기"
                        context.startActivity(Intent.createChooser(intent, chooserTitle))

                        true
                    }

                    R.id.menu_posting -> {
                        Toast.makeText(context, "글 쓰기 클릭", Toast.LENGTH_SHORT).show()

                        val intent = Intent(context, FeedWriteActivity::class.java).apply {
                            putExtra("article_link", article.link)
                            putExtra("article_title", article.title)
                            putExtra("article_imageUrl", article.imageUrl)
                        }
                        context.startActivity(intent)

                        true
                    }

                    else -> false
                }
            }

            popupMenu.setForceShowIcon(true)
            popupMenu.show()
        }
    }

    private fun applyCustomFontAndPadding(menuItem: MenuItem) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.menu_layout, null)
        val icon = view.findViewById<ImageView>(R.id.icon)
        val title = view.findViewById<TextView>(R.id.title)

        icon.setImageDrawable(menuItem.icon)
        title.text = menuItem.title

        menuItem.actionView = view
    }

    override fun getItemCount(): Int = newsArticles.size
}
