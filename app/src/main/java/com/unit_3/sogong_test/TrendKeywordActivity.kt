package com.unit_3.sogong_test

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TrendKeywordActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trend_keyword)

        //인기 검색어에서 클릭시 이 화면으로 넘어와서 연관 뉴스를 쭉 recyclerview 위에 뿌려줘야함.
    }
}