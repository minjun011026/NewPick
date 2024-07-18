package com.unit_3.sogong_test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentsAdapter(private var comments: MutableList<CommentModel>) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(comments[position])
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    fun updateComments(newComments: MutableList<CommentModel>) {
        comments = newComments
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)

        fun bindItems(comment: CommentModel) {
            commentTextView.text = comment.text
        }
    }
}
