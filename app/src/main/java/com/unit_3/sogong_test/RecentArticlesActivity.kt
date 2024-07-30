package com.unit_3.sogong_test

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.unit_3.sogong_test.databinding.ActivityRecentArticlesBinding

class RecentArticlesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecentArticlesBinding
    private lateinit var adapter: RecentArticlesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 설정
        binding.recyclerViewRecentArticles.layoutManager = LinearLayoutManager(this)
        adapter = RecentArticlesAdapter()
        binding.recyclerViewRecentArticles.adapter = adapter

        // 최근 본 뉴스 데이터 로드
        val recentNews = NewsStorage.getRecentNews(this)

        // 데이터가 없을 경우 메시지 표시
        if (recentNews.isEmpty()) {
            binding.noBookmarkedTextview.visibility = View.VISIBLE
            binding.recyclerViewRecentArticles.visibility = View.GONE
        } else {
            binding.noBookmarkedTextview.visibility = View.GONE
            binding.recyclerViewRecentArticles.visibility = View.VISIBLE
            adapter.submitList(recentNews)
        }
    }
}
