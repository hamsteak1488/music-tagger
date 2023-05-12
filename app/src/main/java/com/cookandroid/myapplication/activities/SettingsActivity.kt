package com.cookandroid.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.myapplication.SettingsManager
import com.cookandroid.myapplication.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.applyBtn.setOnClickListener {
            SettingsManager.serverUrl = binding.ipAddressEditText.text.toString()
        }
    }
}