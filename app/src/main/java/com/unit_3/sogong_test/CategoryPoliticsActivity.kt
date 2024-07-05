package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unit_3.sogong_test.databinding.ActivityCategoryPoliticsBinding
import fragments.HomeFragment

class CategoryPoliticsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCategoryPoliticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_politics)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_politics)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val rv : RecyclerView = binding.rv

        val items = ArrayList<NewsModel>()

        items.add(NewsModel("imageUrl1", "title1", "companyName1"))
        items.add(NewsModel("imageUrl2", "title2", "companyName2"))
        items.add(NewsModel("imageUrl3", "title3", "companyName3"))

        val rvAdapter = NewsRVAdapter(items)
        rv.adapter = rvAdapter

        rv.layoutManager = LinearLayoutManager(this)

        // 1. 웹 페이지에서 가져온 내용 보여주기


        // 2. 해당 되는 기사 누르면 웹 페이지로 이동




    }
}