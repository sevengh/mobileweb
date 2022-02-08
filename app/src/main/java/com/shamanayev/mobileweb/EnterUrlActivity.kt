package com.shamanayev.mobileweb

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_enter_url.*

class EnterUrlActivity : AppCompatActivity() {

    private var sharedPreferences: SharedPreferences? = null

    companion object {
        const val demoUrl = "https://www.shamanayev.tk/mobile-web/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_url)

        sharedPreferences = applicationContext.getSharedPreferences("settings", MODE_PRIVATE);

        if (sharedPreferences?.getString("hideUrlOnSetup", "true") == "false") {
            urlEditText.setText(sharedPreferences?.getString("url", ""))
            hide_url_on_setup_checkbox.isChecked = false
        }

        if (sharedPreferences?.getString("screenOrientation", "") != "")
        {
            isSetFixedOrientation_checkbox.isChecked = true

            if (sharedPreferences?.getString("screenOrientation", "PORTRAIT") == "PORTRAIT")
                screen_orientations.setSelection(0)
            else
                screen_orientations.setSelection(1)
        }

        if (sharedPreferences?.getString("fullscreen", "false") == "true")
            fullscreen_checkbox.isChecked = true

        if (sharedPreferences?.getString("keepScreenOn", "false") == "true")
            keepScreenOn_checkbox.isChecked = true

        if (sharedPreferences?.getString("keepInDomain", "true") == "false")
            keepInDomain_checkbox.isChecked = false

        if (sharedPreferences?.getString("autostart", "false") == "true")
            autostart_checkbox.isChecked = true

        if (sharedPreferences?.getString("move_task_back", "true") == "false")
            doNotCloseOnBack_checkbox.isChecked = false

        if (sharedPreferences?.getString("showCustomErrorPage", "true") == "false")
            showCustomErrorPage_checkbox.isChecked = false

        if (sharedPreferences?.getString("disableSelection", "true") == "false")
            disableSelection_checkbox.isChecked = false

        if (sharedPreferences?.getString("refreshSupport", "false") == "true")
            refreshSupport_checkbox.isChecked = true

        enterSample.setOnClickListener {
            urlEditText.setText(demoUrl)
        }

        autostart_checkbox.setOnClickListener {
            if (!autostart_checkbox.isChecked)
                return@setOnClickListener

            Toast
                .makeText(
                    this,
                    getString(R.string.settings_application_autostart_hint),
                    Toast.LENGTH_LONG
                )
                .show()
        }

        enterButton.setOnClickListener {
            val uri: String = urlEditText.text.toString()

            if (uri.isBlank() || (!uri.startsWith("https://") && !uri.startsWith("http://"))) {
                Toast.makeText(this, getString(R.string.settings_wrong_url_text), Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            val screenOrientation =
                when {
                    isSetFixedOrientation_checkbox.isChecked && screen_orientations.selectedItemPosition == 0 -> "PORTRAIT"
                    isSetFixedOrientation_checkbox.isChecked && screen_orientations.selectedItemPosition == 1 -> "LANDSCAPE"
                    else -> ""
                }

            val fullscreen = if (fullscreen_checkbox.isChecked) "true" else "false"
            val moveTaskBack = if (doNotCloseOnBack_checkbox.isChecked) "true" else "false"
            val keepScreenOn = if (keepScreenOn_checkbox.isChecked) "true" else "false"
            val keepInDomain = if (keepInDomain_checkbox.isChecked) "true" else "false"
            val autostart = if (autostart_checkbox.isChecked) "true" else "false"
            val showCustomErrorPage = if (showCustomErrorPage_checkbox.isChecked) "true" else "false"
            val disableSelection = if (disableSelection_checkbox.isChecked) "true" else "false"
            val hideUrlOnSetup = if (hide_url_on_setup_checkbox.isChecked) "true" else "false"
            val refreshSupport = if (refreshSupport_checkbox.isChecked) "true" else "false"

            val editor = sharedPreferences?.edit()
            editor?.putString("url", uri)
            editor?.putString("screenOrientation", screenOrientation)
            editor?.putString("fullscreen", fullscreen)
            editor?.putString("move_task_back", moveTaskBack)
            editor?.putString("keepScreenOn", keepScreenOn)
            editor?.putString("keepInDomain", keepInDomain)
            editor?.putString("autostart", autostart)
            editor?.putString("showCustomErrorPage", showCustomErrorPage)
            editor?.putString("disableSelection", disableSelection)
            editor?.putString("hideUrlOnSetup", hideUrlOnSetup)
            editor?.putString("refreshSupport", refreshSupport)
            editor?.apply()

            startActivity(
                Intent(this, MainActivity::class.java)
            )

            finish()
        }

        clear_text.setOnClickListener { urlEditText.setText("") }
        cameraPermission.setOnClickListener { requestVideoAccess() }
        microphonePermission.setOnClickListener { requestAudioAccess() }
    }

    private fun requestVideoAccess() {
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

            Toast.makeText(
                this,
                getString(R.string.settings_access_granted),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                getString(R.string.settings_access_already_granted),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun requestAudioAccess() {
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

            Toast.makeText(
                this,
                getString(R.string.settings_access_granted),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                getString(R.string.settings_access_already_granted),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
