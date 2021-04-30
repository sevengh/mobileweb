package com.shamanayev.mobileweb

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
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

        sharedPreferences = applicationContext.getSharedPreferences("settings", MODE_PRIVATE)

        if (sharedPreferences?.getString("screenOrientation", "") == "PORTRAIT")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else if (sharedPreferences?.getString("screenOrientation", "") == "LANDSCAPE")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        val url = sharedPreferences?.getString("url", "")

        if (url.isNullOrEmpty()) {
            startActivity(Intent(this, EnterUrlActivity::class.java))
            finish()
            return
        }

        val webSettings = webView.settings
        val userAgent = String.format(
            "%s [%s/%s]",
            webSettings.userAgentString,
            "App Android",
            BuildConfig.VERSION_NAME
        )

        Log.d("userAgent", userAgent)

        webSettings.userAgentString = userAgent
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        if (savedInstanceState != null) {
            this.webView.restoreState(savedInstanceState.getBundle("webViewState")!!);
        } else {
            this.webView.loadUrl(url)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
        outState.putBundle("webViewState", outState);
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }
}
