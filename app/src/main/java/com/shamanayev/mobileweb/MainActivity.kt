package com.shamanayev.mobileweb

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream
import java.net.URI


class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"
    private var sharedPreferences: SharedPreferences? = null

    @SuppressLint("SourceLockedOrientationActivity", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action

        sharedPreferences = applicationContext.getSharedPreferences("settings", MODE_PRIVATE)

        if (sharedPreferences?.getString("fullscreen", "") == "true")
            setFullScreen()

        if (sharedPreferences?.getString("keepScreenOn", "") == "true")
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_main)

        if (sharedPreferences?.getString("refreshSupport", "false") != "true")
        {
            swipeRefresh.isRefreshing = false
            swipeRefresh.isEnabled = false
        }

        swipeRefresh.setOnRefreshListener {
            webView.reload()
            swipeRefresh.isRefreshing = false
        }

        val url: String =
            if (action == "com.shamanayev.mobileweb.Main")
                EnterUrlActivity.demoUrl
            else
                sharedPreferences?.getString("url", "").toString()

        if (sharedPreferences?.getString("disableSelection", "") == "true") {
            webView.setOnLongClickListener { true }
            webView.isLongClickable = false
        }

        this.webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.d(tag, "onPageStarted url: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d(tag, "onPageFinished url: $url")
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                Log.d(tag, "onReceivedError request. Url: " + request.url.toString())

                if (sharedPreferences?.getString("showCustomErrorPage", "") == "true") {
                    try {
                        webView.stopLoading()
                    } catch (e: Exception) {
                    }

                    var string = loadHtml()

                    string = string
                        .replace("%noInternet%", getString(R.string.noInternet))
                        .replace("%reload%", getString(R.string.reload))

                    loadContent(string)
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                Log.d(tag, "shouldOverrideUrlLoading")

                if (sharedPreferences?.getString("keepInDomain", "") == "true"
                    && !isSameDomain(url, request.url.toString())
                ) {
                    var string = loadHtml()

                    string = string
                        .replace("%noInternet%", getString(R.string.noAccess))
                        .replace("%reload%", getString(R.string.clickBack))

                    view.loadData(string, "text/html", "UTF-8")
                } else
                    view.loadUrl(request.url.toString())

                return false
            }

            private fun isSameDomain(urlBase: String?, urlNew: String?): Boolean {
                return getDomainName(urlBase.toString()) == getDomainName(urlNew.toString())
            }

            private fun getDomainName(url: String): String? {
                val uri = URI(url)
                val domain: String? = uri.host
                return if (domain?.startsWith("www.") == true) domain.substring(4) else domain
            }
        }

        requestedOrientation = when {
            sharedPreferences?.getString(
                "screenOrientation",
                ""
            ) == "PORTRAIT" -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            sharedPreferences?.getString(
                "screenOrientation",
                ""
            ) == "LANDSCAPE" -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

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
        webSettings.setAppCacheEnabled(true)
        webSettings.setAppCachePath(this.cacheDir.path)
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
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
            this.webView.restoreState(savedInstanceState.getBundle("webViewState")!!)
        } else {
            this.webView.loadUrl(url)
        }
    }

    private fun loadHtml(): String {
        try {
            val inputStream: InputStream = assets.open("check_internet.html")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            return String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return ""
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
        outState.putBundle("webViewState", bundle)
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
        Log.d(tag, "onBackPressed()")

        if (webView.canGoBack()) {
            webView.goBack()
            return
        }

        if (sharedPreferences?.getString("move_task_back", "") == "true")
            moveTaskToBack(true)
        else
            finish()
    }

    private fun loadContent(content: String) {
        webView.loadData(content, "text/html; charset=utf-8", "UTF-8")
    }
}
