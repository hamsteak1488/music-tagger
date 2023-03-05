package com.cookandroid.myapplication

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cookandroid.myapplication.databinding.ActivityLoadingBinding
import com.cookandroid.myapplication.databinding.ActivityLoginBinding
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

        var ID = pref.getString("id",null)
        var PW = pref.getString("pw",null)
        var AUTO = pref.getBoolean("auto",false)
        if(AUTO){
            if (ID != null) {
                if (PW != null) {
                    auth.signInWithEmailAndPassword(ID, PW)
                        .addOnCompleteListener(this) { login ->
                            if (login.isSuccessful) {
                                Intent(this, MainActivity::class.java).also {
                                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(it)
                                }
                            }
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