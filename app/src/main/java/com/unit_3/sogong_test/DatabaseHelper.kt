package com.unit_3.sogong_test

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DatabaseHelper {

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference.child("bookmarked_news")
    }

    fun addBookmarkedNews(news: BookmarkedNewsModel, callback: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val newsId = database.child(userId).push().key
            if (newsId != null) {
                database.child(userId).child(newsId).setValue(news)
                    .addOnCompleteListener { task ->
                        callback(task.isSuccessful)
                    }
            } else {
                callback(false)
            }
        } else {
            callback(false)
        }
    }

    fun getAllBookmarkedNews(callback: (List<BookmarkedNewsModel>) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newsList = mutableListOf<BookmarkedNewsModel>()
                    for (childSnapshot in snapshot.children) {
                        val news = childSnapshot.getValue(BookmarkedNewsModel::class.java)
                        if (news != null) {
                            newsList.add(news)
                        }
                    }
                    callback(newsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
        } else {
            callback(emptyList())
        }
    }

    fun removeBookmarkedNews(news: BookmarkedNewsModel, callback: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            database.child(userId).orderByChild("link").equalTo(news.link).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.ref.removeValue()
                    }
                    callback(true)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
        } else {
            callback(false)
        }
    }
}
