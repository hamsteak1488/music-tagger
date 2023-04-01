package com.cookandroid.myapplication.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cookandroid.myapplication.R
import com.cookandroid.myapplication.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView


@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var pref:SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)



        setContentView(binding.root)
//Auth part
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

//자동로그인


        // id pw 저장
        //구현 : SharedPreferences 앱의 임시 저장소에 id,pw ox(이메일,pw저장), auto(자동로그인)
        /*val pref: SharedPreferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        val editor: SharedPreferences.Editor = pref.edit();*/
        pref= getSharedPreferences("pref", Activity.MODE_PRIVATE)
        editor = pref.edit()
        val ID = pref.getString("id",null)
        // var PW = pref.getString("pw",null)
        val OX = pref.getBoolean("ox",false)
        var AUTO = pref.getBoolean("auto",false)


        if(OX){
            binding.autoCheckBox.isChecked=true
            binding.userEmail.setText(ID)

        }





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

            // 체크 박스 체크되어 있을 시 로그인 버튼 클릭시 text box의 id pw 저장
            if(binding.autoLogincheckBox.isChecked){
                editor.putString("id",email)
                editor.putString("pw",pw)
                editor.putBoolean("ox",true)
                editor.putBoolean("auto",true)
                editor.apply()
            }
            else if(binding.autoCheckBox.isChecked){
                editor.putString("id",email)
                editor.putBoolean("ox",true)
                editor.putBoolean("auto",binding.autoLogincheckBox.isChecked)
                editor.apply()
            }
            else{
                editor.putBoolean("ox",false)
                editor.apply()
            }

        }
        //버튼 초기 상태 사용 불가, 회색
        // email, pw 저장설정이 켜져있을 경우 버튼 초기 상태 사용 가능, 파랑색
        if (!OX){
            binding.loginBtn.isEnabled = false
            binding.loginBtn.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.gray)
        }
        else{
            binding.loginBtn.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.deepBlue)
        }

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
                        it.putExtra("email", binding.userEmail.text.toString())
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    editor.putString("id",null)
                    editor.putString("pw",null)
                    editor.putBoolean("ox",false)
                    editor.putBoolean("auto",false)
                    editor.apply()
                    Toast.makeText(this, login.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}