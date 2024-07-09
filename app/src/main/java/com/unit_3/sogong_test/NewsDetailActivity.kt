package com.unit_3.sogong_test

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NewsDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)


        // WebView 초기화
        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = WebViewClient()


        // WebView 설정
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true

        // 인텐트에서 링크를 가져와 웹뷰에 로드
        val link = intent.getStringExtra("link")
        link?.let {
            webView.loadUrl(it)
        }



    }
}