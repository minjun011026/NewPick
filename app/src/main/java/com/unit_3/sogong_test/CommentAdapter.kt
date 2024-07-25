//package com.unit_3.sogong_test
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.PopupMenu
//import android.widget.TextView
//import android.widget.Toast
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.Firebase
//import com.google.firebase.auth.auth
//import com.google.firebase.database.database
//
//class CommentsAdapter(private var comments: MutableList<CommentModel>) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
//        return ViewHolder(v)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bindItems(comments[position])
//    }
//
//    override fun getItemCount(): Int {
//        return comments.size
//    }
//
//    fun updateComments(newComments: MutableList<CommentModel>) {
//        comments = newComments
//        notifyDataSetChanged()
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
//
//        fun bindItems(comment: CommentModel) {
//            commentTextView.text = comment.text
//
//            val likeBtn = itemView.findViewById<LinearLayout>(R.id.likeBtn)
//            val replyBtn = itemView.findViewById<TextView>(R.id.replyBtn)
//            val moreVertBtn = itemView.findViewById<ImageView>(R.id.moreVertBtn)
//
//            likeBtn.setOnClickListener {
//
//            }
//
//            replyBtn.setOnClickListener {
//
//            }
//
//
//            moreVertBtn.setOnClickListener {
//
//            }
//
//        }
//    }
//}


package com.unit_3.sogong_test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentsAdapter(private var comments: MutableList<CommentModel>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    fun updateComments(newComments: List<CommentModel>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val uidTextView: TextView = itemView.findViewById(R.id.uidTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        private val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        private val likeBtn: LinearLayout = itemView.findViewById(R.id.likeBtn)
        private val likeCntTextView: TextView = itemView.findViewById(R.id.likeCntTextView)
        private val replyBtn: TextView = itemView.findViewById(R.id.replyBtn)
        private val deleteBtn: Button = itemView.findViewById(R.id.button1)

        fun bind(comment: CommentModel) {
            uidTextView.text = comment.userId
            timeTextView.text = convertTimestampToDate(comment.time)
            commentTextView.text = comment.text
            likeCntTextView.text = comment.likes.toString()

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            likeBtn.setOnClickListener {
                if (comment.likedUsers.contains(userId)) {
                    comment.likes -= 1
                    comment.likedUsers.remove(userId)
                } else {
                    comment.likes += 1
                    comment.likedUsers.add(userId)
                }
                updateCommentLikes(comment)
            }

            deleteBtn.setOnClickListener {
                deleteComment(comment.commentId)
            }

            replyBtn.setOnClickListener {
                // 대댓글 작성 기능 구현
            }
        }

        private fun updateCommentLikes(comment: CommentModel) {
            val feedRef = FirebaseDatabase.getInstance().reference.child("feeds").child(comment.feedId).child("comments").child(comment.commentId)
            feedRef.child("likes").setValue(comment.likes)
            feedRef.child("likedUsers").setValue(comment.likedUsers).addOnCompleteListener {
                likeCntTextView.text = comment.likes.toString()
            }
        }

        private fun deleteComment(commentId: String) {
            val comment = comments[adapterPosition]
            FirebaseDatabase.getInstance().reference.child("feeds").child(comment.feedId).child("comments").child(commentId).removeValue()
        }

        private fun convertTimestampToDate(timestamp: String): String {
            val sdf = SimpleDateFormat("yyyy.MM.dd(E) HH:mm", Locale.KOREA)
            val date = Date(timestamp.toLong())
            return sdf.format(date)
        }
    }
}
