package com.cookandroid.myapplication.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cookandroid.myapplication.databinding.ActivityLoadingBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class Loading : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val pref: SharedPreferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        val ID = pref.getString("id",null)
        val PW = pref.getString("pw",null)
        val AUTO = pref.getBoolean("auto",false)
        if(AUTO){
            if (ID != null && PW != null) {
                auth.signInWithEmailAndPassword(ID, PW)
                    .addOnCompleteListener(this) { login ->
                        if (login.isSuccessful) {
                            val mainIntent = Intent(this, MainActivity::class.java)
                            mainIntent.putExtra("email", ID)
                            mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(mainIntent)
                        }
                }
            }
        } else {
            Intent(this, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }
}