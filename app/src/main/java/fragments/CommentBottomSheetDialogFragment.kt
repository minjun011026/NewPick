package com.unit_3.sogong_test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var postCommentButton: Button
    private lateinit var commentEditText: EditText
    private lateinit var noCommentLayout: LinearLayout

    private lateinit var feedId: String

    companion object {
        fun newInstance(feedId: String): CommentBottomSheetDialogFragment {
            val fragment = CommentBottomSheetDialogFragment()
            val args = Bundle()
            args.putString("feedId", feedId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feedId = arguments?.getString("feedId") ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comment_bottom_sheet_dialog, container, false)

        commentsRecyclerView = view.findViewById(R.id.commentRecyclerView)
        postCommentButton = view.findViewById(R.id.postCommentButton)
        commentEditText = view.findViewById(R.id.commentEditText)
        noCommentLayout = view.findViewById(R.id.no_comment_layout)

        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentsAdapter = CommentsAdapter(mutableListOf())
        commentsRecyclerView.adapter = commentsAdapter

        postCommentButton.setOnClickListener {
            postComment()
        }

        loadComments()

        return view
    }

    private fun postComment() {
        val commentText = commentEditText.text.toString()
        if (commentText.isNotBlank()) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val commentId = FirebaseDatabase.getInstance().reference.child("feeds").child(feedId).child("comments").push().key ?: ""
            val currentTime = System.currentTimeMillis().toString() // Get current time in milliseconds
            val comment = CommentModel(commentId, feedId, userId, commentText, currentTime)

            val feedRef = FirebaseDatabase.getInstance().reference.child("feeds").child(feedId)

            feedRef.child("comments").child(commentId).setValue(comment)
                .addOnSuccessListener {
                    // 댓글 수 증가
                    feedRef.child("commentsCnt").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val commentsCnt = snapshot.getValue(Int::class.java) ?: 0
                            feedRef.child("commentsCnt").setValue(commentsCnt + 1)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // 댓글 수 증가 실패
                        }
                    })

                    commentEditText.text.clear()
                    loadComments() // 댓글 작성 후 댓글 목록을 다시 로드
                }
        }
    }



    private fun loadComments() {
        FirebaseDatabase.getInstance().reference.child("feeds").child(feedId).child("comments").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val comments = mutableListOf<CommentModel>()
                for (commentSnapshot in snapshot.children) {
                    val comment = commentSnapshot.getValue(CommentModel::class.java)
                    if (comment != null) {
                        comments.add(comment)
                    }
                }
                //댓글이 하나라도 작성되면 no_comment_layout이 사라지도록.
                if (comments.isEmpty()) {
                    noCommentLayout.visibility = View.VISIBLE
                    commentsRecyclerView.visibility = View.GONE
                } else {
                    noCommentLayout.visibility = View.GONE
                    commentsRecyclerView.visibility = View.VISIBLE
                }
                commentsAdapter.updateComments(comments)
            }

            override fun onCancelled(error: DatabaseError) {
                // 댓글 로드 실패
            }
        })
    }

    private fun deleteComment(commentId: String) {
        val feedRef = FirebaseDatabase.getInstance().reference.child("feeds").child(feedId)
        feedRef.child("comments").child(commentId).removeValue()
            .addOnSuccessListener {
                // 댓글 수 감소
                feedRef.child("commentsCnt").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val commentsCnt = snapshot.getValue(Int::class.java) ?: 0
                        feedRef.child("commentsCnt").setValue(commentsCnt - 1)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // 댓글 수 감소 실패
                    }
                })
                loadComments() // 댓글 삭제 후 댓글 목록을 다시 로드
            }
    }
}
