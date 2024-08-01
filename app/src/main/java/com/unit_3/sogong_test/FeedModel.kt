package com.unit_3.sogong_test


data class FeedModel(
    var id: String = "",
    var uid: String = "",
    var title: String = "",
    var time: String = "",
    var content: String = "",
    var articleTitle: String? = null,
    var link: String? = null,
    var imageUrl: String? = null,
    var likes: Int = 0,
    var commentsCnt: Int = 0,
    var likedUsers: MutableList<String> = mutableListOf() // MutableList<String> 타입
)
