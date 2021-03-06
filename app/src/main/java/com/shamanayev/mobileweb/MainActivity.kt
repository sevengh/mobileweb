package com.shamanayev.mobileweb

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI


class MainActivity : AppCompatActivity() {

    private var sharedPreferences: SharedPreferences? = null

    @SuppressLint("SourceLockedOrientationActivity", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = applicationContext.getSharedPreferences("settings", MODE_PRIVATE)

        if (sharedPreferences?.getString("fullscreen", "") == "true")
            setFullScreen()

        if (sharedPreferences?.getString("keepScreenOn", "") == "true")
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main)

        val url = sharedPreferences?.getString("url", "")

        this.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                if (sharedPreferences?.getString("keepInDomain", "") == "true" && !isFalse(url, request.url.toString()))
                    view.loadData(getString(R.string.noAccess), "text/html", "UTF-8")
                else
                    view.loadUrl(request.url.toString())

                return false
            }

            private fun isFalse(urlBase: String?, urlNew: String?): Boolean {
                return getDomainName(urlBase.toString()) == getDomainName(urlNew.toString())
            }

            private fun getDomainName(url: String): String? {
                val uri = URI(url)
                val domain: String? = uri.host
                return if (domain?.startsWith("www.") == true) domain?.substring(4) else domain
            }
        }

        if (sharedPreferences?.getString("screenOrientation", "") == "PORTRAIT")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else if (sharedPreferences?.getString("screenOrientation", "") == "LANDSCAPE")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        if (url.isNullOrEmpty()) {
            startActivity(Intent(this, EnterUrlActivity::class.java))
            finish()
            return
        }

        val webSettings = webView.settings

        Log.d("userAgent", webSettings.userAgentString)

        var userAgent = String.format(
            "%s [%s/%s]",
            webSettings.userAgentString,
            "Mobile Web",
            BuildConfig.VERSION_NAME
        )

        userAgent = userAgent.replace("; wv", "")

        Log.d("userAgent", userAgent)

        webSettings.userAgentString = userAgent
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(this.cacheDir.path);
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT;

        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.mediaPlaybackRequiresUserGesture = false

        this.webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                for (r in request.resources) {
                    if (r.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                        requestVideoAccess()

                    if (r.equals(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
                        requestAudioAccess()
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }
        }

        if (savedInstanceState?.getBundle("webViewState") != null) {
            this.webView.restoreState(savedInstanceState.getBundle("webViewState")!!);
        } else {
            this.webView.loadUrl(url)
        }
    }

    fun requestVideoAccess() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                100
            )
        }
    }

    fun requestAudioAccess() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                100
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("MainActivity", "onSaveInstanceState")
        super.onSaveInstanceState(outState)
        val bundle = Bundle()
        webView.saveState(bundle)
        outState.putBundle("webViewState", bundle);
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d("MainActivity", "onRestoreInstanceState")
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }

    private fun setFullScreen() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return
        }

        if (sharedPreferences?.getString("move_task_back", "") == "true")
            moveTaskToBack(true)
        else
            finish()
    }
}
