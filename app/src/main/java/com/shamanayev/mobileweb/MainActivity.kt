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
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return false
            }
        }

        this.webView.settings.javaScriptEnabled = true;

        sharedPreferences = applicationContext.getSharedPreferences("settings", MODE_PRIVATE);

        var url = sharedPreferences?.getString("url", "");

        if (!url.isNullOrEmpty())
            this.webView.loadUrl(url);
        else {
            val intent = Intent(this, EnterUrlActivity::class.java)
            startActivity(intent)
        }
    }
}
