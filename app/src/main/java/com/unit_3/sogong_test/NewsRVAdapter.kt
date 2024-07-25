package com.unit_3.sogong_test

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.SpannableString
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
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fragments.SummaryDialogFragment

class NewsRVAdapter(private val context: Context, private val newsArticles: List<NewsModel>) :
    RecyclerView.Adapter<NewsRVAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
//        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
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
        holder.titleTextView.text = article.title
//        holder.descriptionTextView.text = article.description
        Glide.with(context)
            .load(article.imageUrl)
            .placeholder(R.drawable.no_image)
            .into(holder.newsImageView)

        holder.itemView.setOnClickListener {
            Toast.makeText(context, "기사 클릭", Toast.LENGTH_LONG).show()
//            val intent = Intent(context, WebViewActivity::class.java)
//            intent.putExtra("link", article.link)
//            context.startActivity(intent)
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

        //더보기 버튼
        holder.moreVertBtn.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.moreVertBtn)
            popupMenu.menuInflater.inflate(R.menu.news_popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_bookmark -> {
                        Toast.makeText(context, "북마크 클릭", Toast.LENGTH_SHORT).show()

                        // 데이터베이스에 해당 기사의 데이터(제목과 URL 반드시 포함)을 저장해주는 작업이 필요함.
                        val dbHelper = DatabaseHelper()

                        val newsTitle = article.title
                        val newsUrl = article.link
                        val news = BookmarkedNewsModel(newsTitle, "", 0, "", newsUrl)

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
                        }
                        context.startActivity(intent)

                        true
                    }

                    else -> false
                }
            }

//            //텍스트 숨기는 코드
//            for (i in 0 until popupMenu.menu.size()) {
//                val item = popupMenu.menu.getItem(i)
//                val spannableString = SpannableString("")
//                item.title = spannableString
//            }

            //아이콘 보여주는 코드
            popupMenu.setForceShowIcon(true)
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int = newsArticles.size
}
