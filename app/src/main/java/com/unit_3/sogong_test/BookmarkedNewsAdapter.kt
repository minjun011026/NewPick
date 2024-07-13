package com.unit_3.sogong_test

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class BookmarkedNewsAdapter(private val context: Context, private val newsItems: ArrayList<BookmarkedNewsModel>) :
    RecyclerView.Adapter<BookmarkedNewsAdapter.ViewHolder>() {

    private var itemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookmarked_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(newsItems[position])
    }

    override fun getItemCount(): Int {
        return newsItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val newsTitleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val moreVertBtn: ImageButton = itemView.findViewById(R.id.moreVertBtn)

        fun bind(item: BookmarkedNewsModel) {
            newsTitleTextView.text = item.title

            // Handle item click
            itemView.setOnClickListener {
                itemClickListener?.invoke(adapterPosition)
            }

            moreVertBtn.setOnClickListener {
                showPopupMenu(item)
            }
        }

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
