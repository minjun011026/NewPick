//package com.unit_3.sogong_test
//
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.PopupMenu
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.fragment.app.FragmentActivity
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//import de.hdodenhof.circleimageview.CircleImageView
//
//class FeedRVAdapter(private val items: MutableList<FeedModel>) : RecyclerView.Adapter<FeedRVAdapter.ViewHolder>() {
//    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference.child("feeds")
//    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val usersDb: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedRVAdapter.ViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(R.layout.feed_rv_item, parent, false)
//        return ViewHolder(v)
//    }
//
//    override fun onBindViewHolder(holder: FeedRVAdapter.ViewHolder, position: Int) {
//        holder.bindItems(items[position])
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        fun bindItems(item: FeedModel) {
//            val name = itemView.findViewById<TextView>(R.id.nameTextView)
//            val title = itemView.findViewById<TextView>(R.id.titleTextView)
//            val time = itemView.findViewById<TextView>(R.id.timeTextView)
//            val content = itemView.findViewById<TextView>(R.id.contentTextView)
//            val likeBtn = itemView.findViewById<ImageView>(R.id.likeBtn)
//            val commentBtn = itemView.findViewById<ImageView>(R.id.commentBtn)
//            val likesTextView = itemView.findViewById<TextView>(R.id.likesTextView)
//            val commentsTextView = itemView.findViewById<TextView>(R.id.commentsTextView)
//            val articleTextView = itemView.findViewById<TextView>(R.id.articleTextView)
//            val articleImageView = itemView.findViewById<ImageView>(R.id.articleImageArea)
//            val moreVertBtn = itemView.findViewById<ImageView>(R.id.moreVertBtn)
//            val imageArea = itemView.findViewById<CircleImageView>(R.id.imageArea)
//
//
//
//            // Firebase에서 사용자 닉네임을 조회하여 설정
//            usersDb.child(item.uid).addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        val nickname = snapshot.child("nickname").getValue(String::class.java)
//                        Log.d("FeedRVAdapter", "Nickname for uid ${item.uid}: $nickname")
//                        name.text = nickname ?: "Unknown"
//                    } else {
//                        Log.d("FeedRVAdapter", "User with uid ${item.uid} does not exist.")
//                        name.text = "Unknown"
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // 오류 처리
//                    Log.e("FeedRVAdapter", "Error fetching nickname for uid ${item.uid}: ${error.message}")
//                    name.text = "Unknown"
//                }
//            })
//
//            title.text = item.title
//            time.text = item.time
//            content.text = item.content
//            likesTextView.text = "좋아요 ${item.likes}개"
//            commentsTextView.text = "댓글 ${item.commentsCnt}개 모두 보기"
//
//            articleTextView.text = item.articleTitle
//
//            // Use Glide to load image and handle visibility
//            if (item.imageUrl.isNullOrEmpty()) {
//                articleImageView.visibility = View.GONE
//            } else {
//                articleImageView.visibility = View.VISIBLE
//                Glide.with(itemView.context).load(item.imageUrl).into(articleImageView)
//            }
//
//            // Check if articleTitle is null or empty
//            if (item.articleTitle.isNullOrEmpty()) {
//                articleTextView.visibility = View.GONE
//            } else {
//                articleTextView.visibility = View.VISIBLE
//                articleTextView.text = item.articleTitle
//            }
//
//            moreVertBtn.setOnClickListener {
//                showPopupMenu(it, item)
//            }
//
//            val currentUserId = auth.currentUser?.uid
//
//            // Update like button icon based on user's like status
//            if (item.likedUsers.contains(currentUserId)) {
//                likeBtn.setImageResource(R.drawable.heart)
//            } else {
//                likeBtn.setImageResource(R.drawable.favorite)
//            }
//
//            likeBtn.setOnClickListener {
//                if (currentUserId != null) {
//                    if (!item.likedUsers.contains(currentUserId)) {
//                        // 좋아요 추가
//                        item.likes += 1
//                        item.likedUsers.add(currentUserId)
//                        likesTextView.text = "좋아요 ${item.likes}개"
//                        likeBtn.setImageResource(R.drawable.heart)
//                    } else {
//                        // 좋아요 취소
//                        item.likes -= 1
//                        item.likedUsers.remove(currentUserId)
//                        likesTextView.text = "좋아요 ${item.likes}개"
//                        likeBtn.setImageResource(R.drawable.favorite)
//                    }
//                    updateLikesInDatabase(item)
//                }
//            }
//
//            commentBtn.setOnClickListener {
//                val activity = itemView.context as FragmentActivity
//                val commentFragment = CommentBottomSheetDialogFragment.newInstance(item.id)
//                commentFragment.show(activity.supportFragmentManager, commentFragment.tag)
//            }
//
//            commentsTextView.setOnClickListener {
//                val activity = itemView.context as FragmentActivity
//                val commentFragment = CommentBottomSheetDialogFragment.newInstance(item.id)
//                commentFragment.show(activity.supportFragmentManager, commentFragment.tag)
//            }
//
//            articleTextView.setOnClickListener {
//                val context = itemView.context
//                val intent = Intent(context, WebViewActivity::class.java).apply {
//                    putExtra("link", item.link)
//                }
//                context.startActivity(intent)
//            }
//        }
//
//        private fun updateLikesInDatabase(item: FeedModel) {
//            val itemRef = db.child(item.id)
//            itemRef.child("likes").setValue(item.likes)
//            itemRef.child("likedUsers").setValue(item.likedUsers)
//                .addOnSuccessListener {
//                    // 성공적으로 업데이트됨
//                }
//                .addOnFailureListener { e ->
//                    // 업데이트 실패
//                }
//        }
//
//        private fun showPopupMenu(view: View, item: FeedModel) {
//            val popupMenu = PopupMenu(view.context, view)
//            popupMenu.inflate(R.menu.feed_popup)
//            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
//                when (menuItem.itemId) {
//                    R.id.delete -> {
//                        val currentUserId = auth.currentUser?.uid
//                        if (currentUserId == item.uid) {
//                            showDeleteDialog(view.context, item)
//                        } else {
//                            Toast.makeText(view.context, "삭제 권한이 없습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                        true
//                    }
//                    else -> false
//                }
//            }
//            popupMenu.show()
//        }
//
//        private fun showDeleteDialog(context: Context, item: FeedModel) {
//            val builder = AlertDialog.Builder(context)
//            builder.setTitle("게시물 삭제")
//            builder.setMessage("정말 삭제하시겠습니까?")
//            builder.setPositiveButton("삭제") { _, _ ->
//                deleteFeedItem(item)
//            }
//            builder.setNegativeButton("취소", null)
//            builder.show()
//        }
//
//        private fun deleteFeedItem(item: FeedModel) {
//            db.child(item.id).removeValue()
//                .addOnSuccessListener {
//                    items.remove(item)
//                    notifyDataSetChanged()
//                    Toast.makeText(itemView.context, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(itemView.context, "게시물 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                    Log.e("FeedRVAdapter", "Failed to delete feed item: ${e.message}")
//                }
//        }
//    }
//}
//


package com.unit_3.sogong_test

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class FeedRVAdapter(private val items: MutableList<FeedModel>) : RecyclerView.Adapter<FeedRVAdapter.ViewHolder>() {
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference.child("feeds")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val usersDb: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")

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
            val articleArea = itemView.findViewById<LinearLayout>(R.id.articleArea)
            val commentsTextView = itemView.findViewById<TextView>(R.id.commentsTextView)
            val articleTextView = itemView.findViewById<TextView>(R.id.articleTextView)
            val articleImageView = itemView.findViewById<ImageView>(R.id.articleImageArea)
            val moreVertBtn = itemView.findViewById<ImageView>(R.id.moreVertBtn)
            val imageArea = itemView.findViewById<CircleImageView>(R.id.imageArea)
            val moreLinearLayout = itemView.findViewById<LinearLayout>(R.id.moreLinearLayout)

            articleArea.clipToOutline = true

            // Firebase에서 사용자 닉네임 및 프로필 사진을 조회하여 설정
            usersDb.child(item.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val nickname = snapshot.child("nickname").getValue(String::class.java)
                        val profilePictureUrl = snapshot.child("profile_picture").getValue(String::class.java)

                        Log.d("FeedRVAdapter", "Nickname for uid ${item.uid}: $nickname")
                        Log.d("FeedRVAdapter", "Profile picture URL for uid ${item.uid}: $profilePictureUrl")

                        name.text = nickname ?: "Unknown"
                        if (!profilePictureUrl.isNullOrEmpty()) {
                            if(profilePictureUrl == "URL_OF_DEFAULT_IMAGE"){
                                imageArea.setImageResource(R.drawable.account_circle)
                            }else{
                                Glide.with(itemView.context).load(profilePictureUrl).into(imageArea)
                            }
                        }
                    } else {
                        Log.d("FeedRVAdapter", "User with uid ${item.uid} does not exist.")
                        name.text = "Unknown"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 오류 처리
                    Log.e("FeedRVAdapter", "Error fetching nickname for uid ${item.uid}: ${error.message}")
                    name.text = "Unknown"
                }
            })

            title.text = item.title
            time.text = item.time
            content.text = item.content
            likesTextView.text = "좋아요 ${item.likes}개"
            commentsTextView.text = "댓글 ${item.commentsCnt}개 모두 보기"

            articleTextView.text = item.articleTitle

            // Use Glide to load image and handle visibility
            if (item.imageUrl.isNullOrEmpty()) {
                articleImageView.visibility = View.GONE
            } else {
                articleImageView.visibility = View.VISIBLE
                Glide.with(itemView.context).load(item.imageUrl).into(articleImageView)
            }

            // Check if articleTitle is null or empty
            if (item.articleTitle.isNullOrEmpty()) {
                articleTextView.visibility = View.GONE
            } else {
                articleTextView.visibility = View.VISIBLE
                articleTextView.text = item.articleTitle
            }

            moreVertBtn.setOnClickListener {
                showPopupMenu(it, item)
            }

            val currentUserId = auth.currentUser?.uid

            // Update like button icon based on user's like status
            if (item.likedUsers.contains(currentUserId)) {
                likeBtn.setImageResource(R.drawable.heart)
            } else {
                likeBtn.setImageResource(R.drawable.favorite)
            }

            likeBtn.setOnClickListener {
                if (currentUserId != null) {
                    if (!item.likedUsers.contains(currentUserId)) {
                        // 좋아요 추가
                        item.likes += 1
                        item.likedUsers.add(currentUserId)
                        likesTextView.text = "좋아요 ${item.likes}개"
                        likeBtn.setImageResource(R.drawable.heart)
                    } else {
                        // 좋아요 취소
                        item.likes -= 1
                        item.likedUsers.remove(currentUserId)
                        likesTextView.text = "좋아요 ${item.likes}개"
                        likeBtn.setImageResource(R.drawable.favorite)
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

            moreLinearLayout.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, WebViewActivity::class.java).apply {
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

        private fun showPopupMenu(view: View, item: FeedModel) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.feed_popup)
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.delete -> {
                        val currentUserId = auth.currentUser?.uid
                        if (currentUserId == item.uid) {
                            showDeleteDialog(view.context, item)
                        } else {
                            Toast.makeText(view.context, "삭제 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        private fun showDeleteDialog(context: Context, item: FeedModel) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("게시물 삭제")
            builder.setMessage("정말 삭제하시겠습니까?")
            builder.setPositiveButton("삭제") { _, _ ->
                deleteFeedItem(item)
            }
            builder.setNegativeButton("취소", null)
            builder.show()
        }

        private fun deleteFeedItem(item: FeedModel) {
            db.child(item.id).removeValue()
                .addOnSuccessListener {
                    items.remove(item)
                    notifyDataSetChanged()
                    Toast.makeText(itemView.context, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(itemView.context, "게시물 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    Log.e("FeedRVAdapter", "Failed to delete feed item: ${e.message}")
                }
        }
    }
}
