//package com.unit_3.sogong_test
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.PopupMenu
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//class CommentsAdapter(private var comments: MutableList<CommentModel>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
//        return CommentViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
//        holder.bind(comments[position])
//    }
//
//    override fun getItemCount(): Int = comments.size
//
//    fun updateComments(newComments: List<CommentModel>) {
//        comments.clear()
//        comments.addAll(newComments)
//        notifyDataSetChanged()
//    }
//
//    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val uidTextView: TextView = itemView.findViewById(R.id.uidTextView)
//        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
//        private val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
//        private val likeBtn: ImageView = itemView.findViewById(R.id.likeBtn) // Change to ImageView
//        private val likeCntTextView: TextView = itemView.findViewById(R.id.likeCntTextView)
//        private val replyBtn: TextView = itemView.findViewById(R.id.replyBtn)
//        private val moreVertBtn : ImageView = itemView.findViewById(R.id.moreVertBtn)
//
//
//        fun bind(comment: CommentModel) {
//            uidTextView.text = comment.userId
//            timeTextView.text = convertTimestampToDate(comment.time)
//            commentTextView.text = comment.text
//            likeCntTextView.text = comment.likes.toString()
//
//            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//
//            // Update like button icon based on user's like status
//            if (comment.likedUsers.contains(userId)) {
//                likeBtn.setImageResource(R.drawable.heart) // Change to heart.png
//            } else {
//                likeBtn.setImageResource(R.drawable.favorite) // Default favorite icon
//            }
//
//            likeBtn.setOnClickListener {
//                if (comment.likedUsers.contains(userId)) {
//                    comment.likes -= 1
//                    comment.likedUsers.remove(userId)
//                    likeBtn.setImageResource(R.drawable.favorite)
//                } else {
//                    comment.likes += 1
//                    comment.likedUsers.add(userId)
//                    likeBtn.setImageResource(R.drawable.heart)
//                }
//                updateCommentLikes(comment)
//            }
//
//            moreVertBtn.setOnClickListener {
//                showPopupMenu(it, comment)
//            }
//
//            replyBtn.setOnClickListener {
//                // Implement reply functionality here
//            }
//        }
//
//        private fun updateCommentLikes(comment: CommentModel) {
//            val feedRef = FirebaseDatabase.getInstance().reference.child("feeds").child(comment.feedId).child("comments").child(comment.commentId)
//            feedRef.child("likes").setValue(comment.likes)
//            feedRef.child("likedUsers").setValue(comment.likedUsers).addOnCompleteListener {
//                likeCntTextView.text = comment.likes.toString()
//            }
//        }
//
//        private fun deleteComment(comment: CommentModel) {
//            val position = comments.indexOf(comment)
//            if (position != -1) {
//                val feedRef = FirebaseDatabase.getInstance().reference.child("feeds").child(comment.feedId)
//                feedRef.child("comments").child(comment.commentId).removeValue().addOnCompleteListener {
//                    feedRef.child("commentsCnt").addListenerForSingleValueEvent(object :
//                        ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            val commentsCnt = snapshot.getValue(Int::class.java) ?: 0
//                            feedRef.child("commentsCnt").setValue(commentsCnt - 1)
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            // Handle error if needed
//                        }
//                    })
//                    comments.removeAt(position)
//                    notifyItemRemoved(position)
//                }
//            }
//        }
//        private fun convertTimestampToDate(timestamp: String): String {
//            val sdf = SimpleDateFormat("yyyy.MM.dd(E) HH:mm", Locale.KOREA)
//            val date = Date(timestamp.toLong())
//            return sdf.format(date)
//        }
//
//        private fun showPopupMenu(view: View, comment: CommentModel) {
//            val popupMenu = PopupMenu(view.context, view)
//            popupMenu.inflate(R.menu.feed_popup)
//            popupMenu.setOnMenuItemClickListener { item ->
//                when (item.itemId) {
//                    R.id.delete -> {
//                        deleteComment(comment)
//                        true
//                    }
//                    else -> false
//                }
//            }
//            popupMenu.show()
//        }
//    }
//}
//


package com.unit_3.sogong_test

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        private val likeBtn: ImageView = itemView.findViewById(R.id.likeBtn)
        private val likeCntTextView: TextView = itemView.findViewById(R.id.likeCntTextView)
        private val replyBtn: TextView = itemView.findViewById(R.id.replyBtn)
        private val moreVertBtn: ImageView = itemView.findViewById(R.id.moreVertBtn)

        fun bind(comment: CommentModel) {
            uidTextView.text = comment.userId
            timeTextView.text = convertTimestampToDate(comment.time)
            commentTextView.text = comment.text
            likeCntTextView.text = comment.likes.toString()

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            // Update like button icon based on user's like status
            if (comment.likedUsers.contains(userId)) {
                likeBtn.setImageResource(R.drawable.heart) // Change to heart.png
            } else {
                likeBtn.setImageResource(R.drawable.favorite) // Default favorite icon
            }

            likeBtn.setOnClickListener {
                if (comment.likedUsers.contains(userId)) {
                    comment.likes -= 1
                    comment.likedUsers.remove(userId)
                    likeBtn.setImageResource(R.drawable.favorite)
                } else {
                    comment.likes += 1
                    comment.likedUsers.add(userId)
                    likeBtn.setImageResource(R.drawable.heart)
                }
                updateCommentLikes(comment)
            }

            moreVertBtn.setOnClickListener {
                showPopupMenu(it, comment, itemView.context)
            }

            replyBtn.setOnClickListener {
                // Implement reply functionality here
            }
        }

        private fun updateCommentLikes(comment: CommentModel) {
            val feedRef = FirebaseDatabase.getInstance().reference.child("feeds").child(comment.feedId).child("comments").child(comment.commentId)
            feedRef.child("likes").setValue(comment.likes)
            feedRef.child("likedUsers").setValue(comment.likedUsers).addOnCompleteListener {
                likeCntTextView.text = comment.likes.toString()
            }
        }

        private fun deleteComment(comment: CommentModel) {
            val position = comments.indexOf(comment)
            if (position != -1) {
                val feedRef = FirebaseDatabase.getInstance().reference.child("feeds").child(comment.feedId)
                feedRef.child("comments").child(comment.commentId).removeValue().addOnCompleteListener {
                    feedRef.child("commentsCnt").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val commentsCnt = snapshot.getValue(Int::class.java) ?: 0
                            feedRef.child("commentsCnt").setValue(commentsCnt - 1)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error if needed
                        }
                    })
                    comments.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }

        private fun convertTimestampToDate(timestamp: String): String {
            val sdf = SimpleDateFormat("yyyy.MM.dd(E) HH:mm", Locale.KOREA)
            val date = Date(timestamp.toLong())
            return sdf.format(date)
        }

        private fun showPopupMenu(view: View, comment: CommentModel, context: Context) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.feed_popup)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        if (comment.userId == userId) {
                            showDeleteConfirmationDialog(context, comment)
                        } else {
                            Toast.makeText(context, "자신이 작성한 글이 아닙니다.", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        private fun showDeleteConfirmationDialog(context: Context, comment: CommentModel) {
            AlertDialog.Builder(context)
                .setMessage("삭제하시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    deleteComment(comment)
                }
                .setNegativeButton("아니요", null)
                .show()
        }
    }
}

