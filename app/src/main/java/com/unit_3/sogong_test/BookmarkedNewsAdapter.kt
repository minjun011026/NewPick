package com.unit_3.sogong_test

import android.content.Context
import android.content.Intent
import android.os.Build
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

class BookmarkedNewsAdapter(private val context: Context, private val newsItems: ArrayList<BookmarkedNewsModel>) :
    RecyclerView.Adapter<BookmarkedNewsAdapter.ViewHolder>() {

    private var itemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_rv_item, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(newsItems[position])
    }

    override fun getItemCount(): Int {
        return newsItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val newsTitleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val moreVertBtn: ImageButton = itemView.findViewById(R.id.moreVertBtn)
        val newsArea = itemView.findViewById<LinearLayout>(R.id.newsArea)
        val newsImage = itemView.findViewById<ImageView>(R.id.newsImageView)

        @RequiresApi(Build.VERSION_CODES.Q)
        fun bind(item: BookmarkedNewsModel) {
            newsTitleTextView.text = item.title

            // Glide를 사용하여 이미지 로드
            if (item.imageUrl.isNullOrEmpty()) {
                newsImage.visibility = View.GONE // 이미지 URL이 없는 경우 이미지뷰를 숨깁니다.
            } else {
                newsImage.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.no_image) // 로딩 중에 표시할 기본 이미지
                    .into(newsImage)
            }

            newsArea.setOnClickListener {
                val activity = itemView.context as? FragmentActivity
                activity?.let {
                    val dialogFragment = SummaryDialogFragment.newInstance(item.link)
                    dialogFragment.show(it.supportFragmentManager, "SummaryDialogFragment")
                }
            }


            moreVertBtn.setOnClickListener {
                showPopupMenu(item)
            }
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun showPopupMenu(item: BookmarkedNewsModel) {
            val popupMenu = PopupMenu(context, moreVertBtn)
            popupMenu.menuInflater.inflate(R.menu.bookmark_popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_bookmark -> {
                        Toast.makeText(context, "북마크 삭제 클릭", Toast.LENGTH_SHORT).show()
                        val dbHelper = DatabaseHelper()
                        dbHelper.removeBookmarkedNews(item) { isSuccess ->
                            if (isSuccess) {
                                Toast.makeText(context, "기사가 북마크에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                newsItems.remove(item)
                                notifyDataSetChanged()
                            } else {
                                Toast.makeText(context, "북마크 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        true
                    }
                    R.id.menu_share_link -> {
                        Toast.makeText(context, "링크 공유 클릭", Toast.LENGTH_SHORT).show()
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        val sharedUrl = item.link
                        val content = "친구가 링크를 공유했어요!\n어떤 링크인지 들어가서 확인해볼까요?"
                        intent.putExtra(Intent.EXTRA_TEXT, "$content\n\n$sharedUrl")
                        val chooserTitle = "친구에게 공유하기"
                        context.startActivity(Intent.createChooser(intent, chooserTitle))
                        true
                    }
                    else -> false
                }
            }
            popupMenu.setForceShowIcon(true)
            popupMenu.show()
        }
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        itemClickListener = listener
    }
}
