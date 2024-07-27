package com.unit_3.sogong_test

data class FeedModel(
    val id: String = "", // 게시물의 고유 ID
    val uid: String = "", // 사용자의 ID
    val title: String = "", // 게시물 제목
    val time: String = "", // 게시물 작성 시간
    val content: String = "", // 게시물 내용
    val articleTitle : String? ="", //기사 제목
    val link : String? ="", //기사 링크
    val imageUrl : String? = "", //기사 이미지 링크
    var likes: Int = 0, // 좋아요 개수
    var commentsCnt: Int = 0, // 댓글 개수
    val likedUsers: MutableList<String> = mutableListOf(), // 좋아요를 누른 사용자 ID 목록
    val comments: Map<String, CommentModel> = emptyMap() // 댓글 목록
)
