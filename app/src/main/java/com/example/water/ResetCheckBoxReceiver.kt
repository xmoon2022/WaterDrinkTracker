package com.example.water
import android.content.BroadcastReceiver
import android.content.Context;
import android.content.Intent;
import android.util.Log
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ResetCheckBoxReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ResetCheckBoxReceiver", "onReceive() called with: context = [$context], intent = [$intent]")
        val localIntent = Intent("RESET_CHECK_BOXES")
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
        Log.d("ResetCheckBoxReceiver", "Local broadcast sent.")
    }
}