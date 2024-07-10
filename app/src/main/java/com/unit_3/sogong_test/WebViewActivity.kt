package com.unit_3.sogong_test

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {
    private val TAG = "WebViewActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)


        // WebView 초기화
        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = SSLWebViewClient()


        // WebView 설정
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.domStorageEnabled = true


        // 인텐트에서 링크를 가져와 웹뷰에 로드
        val link = intent.getStringExtra("link")
        link?.let {
            Log.d(TAG, "Loading URL: $it")
            webView.loadUrl(it)
        } ?: run {
            Log.e(TAG, "No link received from Intent")
        }


    }
}