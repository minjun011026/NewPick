package com.unit_3.sogong_test

data class CommentModel(
    val commentId: String = "",
    val feedId: String = "", // Add this line
    val userId: String = "",
    val text: String = "",
    val time: String = "", // 댓글 작성 시간
    var likes: Int = 0, // 좋아요 개수
    val likedUsers: MutableList<String> = mutableListOf(), // 좋아요를 누른 사용자 ID 목록
    val comments: Map<String, CommentModel> = emptyMap() // 대댓글 목록
)
