package com.unit_3.sogong_test

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class KeywordWebViewActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyword_webview)

        val getLink = intent.getStringExtra("링크")

        val webView = findViewById<WebView>(R.id.webview)
        webView.loadUrl(getLink.toString())

    }
}