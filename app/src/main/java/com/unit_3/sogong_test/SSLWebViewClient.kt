package com.unit_3.sogong_test

import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient

class SSLWebViewClient : WebViewClient() {

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        // SSL 에러를 무시하고 페이지를 계속 로드할 수 있도록 처리
        handler?.proceed()
    }

}