package com.shamanayev.mobileweb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootUpReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences = context.getSharedPreferences(
            "settings",
            Context.MODE_PRIVATE
        )

        val isAutoStartEnabled = sharedPreferences?.getString(
            "autostart",
            ""
        ) == "true"

        if (intent.action == Intent.ACTION_BOOT_COMPLETED && isAutoStartEnabled) {
            val serviceIntent = Intent(context, MainActivity::class.java)
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(serviceIntent)
        }
    }

}