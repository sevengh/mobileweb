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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_url)

        sharedPreferences = applicationContext.getSharedPreferences("settings", MODE_PRIVATE);

        enterSample.setOnClickListener {
            urlEditText.setText("https://www.shamanayev.tk/mobile-web/")
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

            val fullscreen = if (fullscreen_checkbox.isChecked) "true" else ""
            val moveTaskBack = if (doNotCloseOnBack_checkbox.isChecked) "true" else ""
            val keepScreenOn = if (keepScreenOn_checkbox.isChecked) "true" else ""
            val keepInDomain = if (keepInDomain_checkbox.isChecked) "true" else ""
            val autostart = if (autostart_checkbox.isChecked) "true" else ""
            val showCustomErrorPage = if (showCustomErrorPage_checkbox.isChecked) "true" else ""
            val disableSelection = if (disableSelection_checkbox.isChecked) "true" else ""

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
