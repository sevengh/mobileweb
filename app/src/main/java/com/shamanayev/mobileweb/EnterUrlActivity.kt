package com.shamanayev.mobileweb

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_enter_url.*

class EnterUrlActivity : AppCompatActivity() {

    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_url)

        sharedPreferences = applicationContext.getSharedPreferences("settings", MODE_PRIVATE);

        enterButton.setOnClickListener(){
            val editor = sharedPreferences?.edit()

            editor?.putString("url", urlEditText.text.toString());
            editor?.apply();

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
