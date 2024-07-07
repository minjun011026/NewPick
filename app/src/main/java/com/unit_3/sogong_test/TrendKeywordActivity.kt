package com.unit_3.sogong_test

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.unit_3.sogong_test.databinding.ActivityTrendKeywordBinding

class TrendKeywordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrendKeywordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrendKeywordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //인기 검색어에서 클릭시 이 화면으로 넘어와서 연관 뉴스를 쭉 recyclerview 위에 뿌려줘야함.

        // 인텐트에서 전달받은 키워드 가져오기
        val keyword = intent.getStringExtra("keyword_title")

        // 가져온 키워드를 TextView에 설정
        binding.keywordTextView.text = "$keyword"


        //네이버 뉴스 검색 API로 keyword에 해당하는 뉴스 가져와서 recyclerview에 올리기





        binding.backBtn.setOnClickListener {
            navigateBackToMainActivity()
        }
    }
        private fun navigateBackToMainActivity() {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }
}