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

        sharedPreferences = applicationContext.getSharedPreferences("settings", MODE_PRIVATE);

        val url = sharedPreferences?.getString("url", "");

        if (url.isNullOrEmpty()) {
            startActivity(Intent(this, EnterUrlActivity::class.java))
            finish();
            return;
        }

        if (savedInstanceState == null) {
            this.webView.settings.javaScriptEnabled = true;
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
