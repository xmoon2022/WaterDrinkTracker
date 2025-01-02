package com.example.water
import android.content.BroadcastReceiver
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

class ResetCheckBoxReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (context is AppCompatActivity) {
            val activity = context as MainActivity
            activity.resetCheckBoxes()
        }
    }
}