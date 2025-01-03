package com.example.water

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.water.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var alarmManager: AlarmManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recover()
        setupAlarm()
        binding.resetbutton.setOnClickListener{
            resetCheckBoxes()
        }
    }

    private fun setupAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ResetCheckBoxReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 4)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
    }

    private fun recover() {
        sharedPreferences = getSharedPreferences("checkbox_states", MODE_PRIVATE)
        // 恢复之前保存的状态
        val isCheckBox0Checked = sharedPreferences.getBoolean("checkBox0", false)
        val isCheckBox1Checked = sharedPreferences.getBoolean("checkBox1", false)
        val isCheckBox2Checked = sharedPreferences.getBoolean("checkBox2", false)
        val isCheckBox3Checked = sharedPreferences.getBoolean("checkBox3", false)
        val isCheckBox4Checked = sharedPreferences.getBoolean("checkBox4", false)
        val isCheckBox5Checked = sharedPreferences.getBoolean("checkBox5", false)
        val isCheckBox6Checked = sharedPreferences.getBoolean("checkBox6", false)
        val isCheckBox7Checked = sharedPreferences.getBoolean("checkBox7", false)
        binding.checkBox0.isChecked = isCheckBox0Checked
        binding.checkBox1.isChecked = isCheckBox1Checked
        binding.checkBox2.isChecked = isCheckBox2Checked
        binding.checkBox3.isChecked = isCheckBox3Checked
        binding.checkBox4.isChecked = isCheckBox4Checked
        binding.checkBox5.isChecked = isCheckBox5Checked
        binding.checkBox6.isChecked = isCheckBox6Checked
        binding.checkBox7.isChecked = isCheckBox7Checked
    }

    fun resetCheckBoxes() {
        binding.checkBox0.isChecked = false
        binding.checkBox1.isChecked = false
        binding.checkBox2.isChecked = false
        binding.checkBox3.isChecked = false
        binding.checkBox4.isChecked = false
        binding.checkBox5.isChecked = false
        binding.checkBox6.isChecked = false
        binding.checkBox7.isChecked = false
    }

    override fun onPause() {
        super.onPause()
        val editor = sharedPreferences.edit()
        editor.putBoolean("checkBox0", binding.checkBox0.isChecked)
        editor.putBoolean("checkBox1", binding.checkBox1.isChecked)
        editor.putBoolean("checkBox2", binding.checkBox2.isChecked)
        editor.putBoolean("checkBox3", binding.checkBox3.isChecked)
        editor.putBoolean("checkBox4", binding.checkBox4.isChecked)
        editor.putBoolean("checkBox5", binding.checkBox5.isChecked)
        editor.putBoolean("checkBox6", binding.checkBox6.isChecked)
        editor.putBoolean("checkBox7", binding.checkBox7.isChecked)
        editor.apply()
    }
}