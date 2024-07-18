package com.unit_3.sogong_test

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FeedRVAdapter(private val items: MutableList<FeedModel>) : RecyclerView.Adapter<FeedRVAdapter.ViewHolder>() {
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference.child("feeds")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedRVAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.feed_rv_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: FeedRVAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: FeedModel) {
            val name = itemView.findViewById<TextView>(R.id.nameTextView)
            val title = itemView.findViewById<TextView>(R.id.titleTextView)
            val time = itemView.findViewById<TextView>(R.id.timeTextView)
            val content = itemView.findViewById<TextView>(R.id.contentTextView)
            val likeBtn = itemView.findViewById<ImageView>(R.id.likeBtn)
            val commentBtn = itemView.findViewById<ImageView>(R.id.commentBtn)
            val likesTextView = itemView.findViewById<TextView>(R.id.likesTextView)
            val commentsTextView = itemView.findViewById<TextView>(R.id.commentsTextView)
            val articleTextView = itemView.findViewById<TextView>(R.id.articleTextView)

            name.text = item.uid
            title.text = item.title
            time.text = item.time
            content.text = item.content
            likesTextView.text = "좋아요 ${item.likes}개"
            commentsTextView.text = "댓글 ${item.commentsCnt}개 모두 보기"
            articleTextView.text = item.articleTitle

            // Check if articleTitle is null or empty
            if (item.articleTitle.isNullOrEmpty()) {
                articleTextView.visibility = View.GONE
            } else {
                articleTextView.visibility = View.VISIBLE
                articleTextView.text = item.articleTitle
            }

            likeBtn.setOnClickListener {
                val currentUserId = auth.currentUser?.uid
                if (currentUserId != null) {
                    if (!item.likedUsers.contains(currentUserId)) {
                        // 좋아요 추가
                        item.likes += 1
                        item.likedUsers.add(currentUserId)
                        likesTextView.text = "좋아요 ${item.likes}개"
                    } else {
                        // 좋아요 취소
                        item.likes -= 1
                        item.likedUsers.remove(currentUserId)
                        likesTextView.text = "좋아요 ${item.likes}개"
                    }
                    updateLikesInDatabase(item)
                }
            }

            commentBtn.setOnClickListener {
                val activity = itemView.context as FragmentActivity
                val commentFragment = CommentBottomSheetDialogFragment.newInstance(item.id)
                commentFragment.show(activity.supportFragmentManager, commentFragment.tag)
            }

            commentsTextView.setOnClickListener {
                val activity = itemView.context as FragmentActivity
                val commentFragment = CommentBottomSheetDialogFragment.newInstance(item.id)
                commentFragment.show(activity.supportFragmentManager, commentFragment.tag)
            }

            articleTextView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context,WebViewActivity::class.java).apply {
                    putExtra("link", item.link)
                }
                context.startActivity(intent)
            }




        }

        private fun updateLikesInDatabase(item: FeedModel) {
            val itemRef = db.child(item.id)
            itemRef.child("likes").setValue(item.likes)
            itemRef.child("likedUsers").setValue(item.likedUsers)
                .addOnSuccessListener {
                    // 성공적으로 업데이트됨
                }
                .addOnFailureListener { e ->
                    // 업데이트 실패
                }
        }
    }
}
