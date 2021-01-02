package com.shamanayev.mobileweb

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_enter_url.*

class EnterUrlActivity : AppCompatActivity() {

    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_url)

        sharedPreferences = applicationContext.getSharedPreferences("settings", MODE_PRIVATE);

        enterButton.setOnClickListener {
            val uri: String = urlEditText.text.toString()

            if (uri.isBlank() || (!uri.startsWith("https://") && !uri.startsWith("http://"))) {
                Toast.makeText(this, getString(R.string.settings_wrong_url_text), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val editor = sharedPreferences?.edit()
            editor?.putString("url", uri)
            editor?.apply()

            startActivity(
                Intent(this, MainActivity::class.java)
            )

            finish()
        }
    }
}
