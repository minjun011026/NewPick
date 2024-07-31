//package com.unit_3.sogong_test
//
//import android.content.Intent
//import android.os.Build
//import android.text.Spannable
//import android.text.SpannableString
//import android.text.style.StyleSpan
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.PopupMenu
//import android.widget.TextView
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.fragment.app.FragmentActivity
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import fragments.SummaryDialogFragment
//
//class KeywordNewsAdapter(val newsItems: ArrayList<KeywordNewsModel>) : RecyclerView.Adapter<KeywordNewsAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordNewsAdapter.ViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(R.layout.news_rv_item, parent, false)
//        return ViewHolder(v)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    override fun onBindViewHolder(holder: KeywordNewsAdapter.ViewHolder, position: Int) {
//        Log.d("KeywordNewsAdapter", "onBindViewHolder called for position $position")
//        holder.bindItems(newsItems[position])
//    }
//
//    override fun getItemCount(): Int {
//        return newsItems.size
//    }
//
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        @RequiresApi(Build.VERSION_CODES.Q)
//        fun bindItems(item: KeywordNewsModel) {
//            val newsArea = itemView.findViewById<LinearLayout>(R.id.newsArea)
//            val newsTitle = itemView.findViewById<TextView>(R.id.titleTextView)
//            val newsImage = itemView.findViewById<ImageView>(R.id.newsImageView)
//            val moreVertBtn = itemView.findViewById<ImageButton>(R.id.moreVertBtn)
//
//            //둥글게
//            newsImage.clipToOutline = true
//
//            val keyword = item.keyword
//
//            // Create a SpannableString for the title
//            val spannableTitle = SpannableString(item.title)
//            val keywordStartIndex = item.title.indexOf(keyword, ignoreCase = true)
//
//            if (keywordStartIndex != -1) {
//                val keywordEndIndex = keywordStartIndex + keyword.length
//                spannableTitle.setSpan(
//                    StyleSpan(android.graphics.Typeface.BOLD),
//                    keywordStartIndex,
//                    keywordEndIndex,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//            }
//
//            newsTitle.text = spannableTitle
//
//            newsArea.setOnClickListener {
//                val activity = itemView.context as? FragmentActivity
//                activity?.let {
//                    val dialogFragment = SummaryDialogFragment.newInstance(item.link)
//                    dialogFragment.show(it.supportFragmentManager, "SummaryDialogFragment")
//                }
//            }
//
//            // Glide를 사용하여 이미지 로드
//            if (item.imageUrl.isNullOrEmpty()) {
//                newsImage.visibility = View.GONE // 이미지 URL이 없는 경우 이미지뷰를 숨깁니다.
//            } else {
//                newsImage.visibility = View.VISIBLE
//                Glide.with(itemView.context)
//                    .load(item.imageUrl)
//                    .placeholder(R.drawable.no_image) // 로딩 중에 표시할 기본 이미지
//                    .into(newsImage)
//            }
//
//            moreVertBtn.setOnClickListener {
//                Log.d("KeywordNewsAdapter", "moreVertBtn clicked")
//                showPopupMenu(it, item)
//            }
//        }
//
//        @RequiresApi(Build.VERSION_CODES.Q)
//        private fun showPopupMenu(view: View, article: KeywordNewsModel) {
//            val context = itemView.context
//            val popupMenu = PopupMenu(context, view)
//            popupMenu.inflate(R.menu.news_popup)
//
//            popupMenu.setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//                    R.id.menu_bookmark -> {
//                        Log.d("KeywordNewsAdapter", "menu_bookmark clicked")
//                        Toast.makeText(context, "북마크 클릭", Toast.LENGTH_SHORT).show()
//
//                        // 데이터베이스에 해당 기사의 데이터(제목과 URL 반드시 포함)을 저장해주는 작업이 필요함.
//                        val dbHelper = DatabaseHelper()
//
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
//                        Log.d("KeywordNewsAdapter", "menu_share_link clicked")
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
//                        Log.d("KeywordNewsAdapter", "menu_posting clicked")
//                        Toast.makeText(context, "글 쓰기 클릭", Toast.LENGTH_SHORT).show()
//
//                        // 로그 추가
//                        Log.d("KeywordNewsAdapter", "Link: ${article.link}, Title: ${article.title}, ImageUrl: ${article.imageUrl}")
//
//                        val intent = Intent(context, FeedWriteActivity::class.java).apply {
//                            putExtra("article_link", article.link)
//                            putExtra("article_title", article.title)
//                            putExtra("article_imageUrl", article.imageUrl)
//
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
//            // 아이콘 보여주는 코드
//            popupMenu.setForceShowIcon(true)
//            popupMenu.show()
//        }
//    }
//}


package com.unit_3.sogong_test

import android.content.Intent
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class KeywordNewsAdapter(private var items: MutableList<KeywordNewsModel>) : RecyclerView.Adapter<KeywordNewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_rv_item, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("KeywordNewsAdapter", "onBindViewHolder called for position $position")
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateNews(newItems: List<KeywordNewsModel>) {
        items = newItems.toMutableList() // Ensure items is a MutableList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @RequiresApi(Build.VERSION_CODES.Q)
        fun bindItems(item: KeywordNewsModel) {
            val newsArea = itemView.findViewById<LinearLayout>(R.id.newsArea)
            val newsTitle = itemView.findViewById<TextView>(R.id.titleTextView)
            val newsImage = itemView.findViewById<ImageView>(R.id.newsImageView)
            val moreVertBtn = itemView.findViewById<ImageButton>(R.id.moreVertBtn)

            // Set image clipping to outline if supported
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                newsImage.clipToOutline = true
            }

            val keyword = item.keyword.orEmpty()
            val spannableTitle = SpannableString(item.title)

            if (keyword.isNotEmpty()) {
                val keywordStartIndex = item.title.indexOf(keyword, ignoreCase = true)
                if (keywordStartIndex != -1) {
                    val keywordEndIndex = keywordStartIndex + keyword.length
                    spannableTitle.setSpan(
                        StyleSpan(android.graphics.Typeface.BOLD),
                        keywordStartIndex,
                        keywordEndIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            newsTitle.text = spannableTitle

            newsArea.setOnClickListener {
                val activity = itemView.context as? FragmentActivity
                activity?.let {
                    val dialogFragment = SummaryDialogFragment.newInstance(item.link)
                    dialogFragment.show(it.supportFragmentManager, "SummaryDialogFragment")
                }
            }

            // Load image with Glide and handle errors
            if (item.imageUrl.isNullOrEmpty()) {
                newsImage.visibility = View.GONE
            } else {
                newsImage.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image) // Add an error placeholder
                    .into(newsImage)
            }

            moreVertBtn.setOnClickListener {
                Log.d("KeywordNewsAdapter", "moreVertBtn clicked")
                showPopupMenu(it, item)
            }
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun showPopupMenu(view: View, article: KeywordNewsModel) {
            val context = itemView.context
            val popupMenu = PopupMenu(context, view)
            popupMenu.inflate(R.menu.news_popup)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_bookmark -> {
                        Log.d("KeywordNewsAdapter", "menu_bookmark clicked")
                        Toast.makeText(context, "북마크 클릭", Toast.LENGTH_SHORT).show()

                        // Add to database
                        val dbHelper = DatabaseHelper()

                        val news = BookmarkedNewsModel(article.title, article.link, article.imageUrl)
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
                        Log.d("KeywordNewsAdapter", "menu_share_link clicked")
                        Toast.makeText(context, "링크 공유 클릭", Toast.LENGTH_SHORT).show()

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "친구가 링크를 공유했어요!\n어떤 링크인지 들어가서 확인해볼까요?\n\n${article.link}")
                        }
                        val chooserTitle = "친구에게 공유하기"
                        context.startActivity(Intent.createChooser(intent, chooserTitle))

                        true
                    }

                    R.id.menu_posting -> {
                        Log.d("KeywordNewsAdapter", "menu_posting clicked")
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
}
