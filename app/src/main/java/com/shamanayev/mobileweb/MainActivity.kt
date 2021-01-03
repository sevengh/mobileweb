package com.shamanayev.mobileweb

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var sharedPreferences: SharedPreferences? = null

    private val userAgent =
        "Mozilla/5.0 (Linux; Android " + android.os.Build.VERSION.RELEASE + "; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                view.loadUrl(request.url.toString())
                return false
            }
        }

        sharedPreferences = applicationContext.getSharedPreferences("settings", MODE_PRIVATE)

        val url = sharedPreferences?.getString("url", "")

        if (url.isNullOrEmpty()) {
            startActivity(Intent(this, EnterUrlActivity::class.java))
            finish();
            return;
        }

        if (savedInstanceState == null) {
            this.webView.settings.javaScriptEnabled = true;
            this.webView.settings.userAgentString = userAgent
            this.webView.loadUrl(url);
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }
}
