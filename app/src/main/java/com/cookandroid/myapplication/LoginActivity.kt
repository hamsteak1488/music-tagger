package com.cookandroid.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.cookandroid.myapplication.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jakewharton.rxbinding2.widget.RxTextView

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
//자동 로그인
    override fun onStart() { //로그인 액티비티 시작시 유저 체크 후 유저 확인시 자동 로그인
        super.onStart()
        moveMainPage(auth?.currentUser)
    }
    fun moveMainPage(user: FirebaseUser?){
        if(user!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//Auth part
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

//stream part

        //ID란 공백 시 공백 알림 호출
        val emailStream = RxTextView.textChanges(binding.userEmail)
            .skipInitialValue()
            .map{email->
                email.isEmpty()
            }
        emailStream.subscribe{
            showTextExistAlert(it, "Email")
        }

        ///PW란 공백 시 공백 알림 호출
        val pwStream = RxTextView.textChanges(binding.userPW)
            .skipInitialValue()
            .map{ pw ->
                pw.isEmpty()
            }
        pwStream.subscribe{
            showTextExistAlert(it, "Password")
        }

//Button bindings

        //로그인 버튼 -> MainActivity
        binding.loginBtn.setOnClickListener{
            val email = binding.userEmail.text.toString().trim()
            val pw = binding.userPW.text.toString().trim()
            loginUser(email, pw)
            //startActivity(Intent(this, MainActivity::class.java))
        }
        //버튼 초기 상태 사용 불가, 회색
        binding.loginBtn.isEnabled = false
        binding.loginBtn.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.gray)
        //레지스터 버튼 -> RegisterActivity
        binding.registerBtn.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

//로그인 버튼 제어
        val invalidFieldsStream = io.reactivex.Observable.combineLatest(
            emailStream,
            pwStream,
            {emailInvalid: Boolean, pwInvalid: Boolean->
                !emailInvalid && !pwInvalid
            })
        ///inValid = true면 버튼 사용 가능, 파란색
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                binding.loginBtn.isEnabled = true
                binding.loginBtn.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.deepBlue)
            }
        }
    }

    //Alert part
    private fun showTextExistAlert(isNotValid: Boolean, text: String){
        if(text=="Email")
            binding.userEmail.error = if(isNotValid) "이메일을 입력하세요" else null
        else if(text=="Password")
            binding.userPW.error = if(isNotValid) "비밀번호를 입력하세요" else null
    }

    private fun loginUser(email: String, pw: String) {

        auth.signInWithEmailAndPassword(email, pw)
            .addOnCompleteListener(this) { login ->
                if (login.isSuccessful) {
                    Intent(this, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {

                    Toast.makeText(this, login.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}