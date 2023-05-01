package com.cookandroid.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.cookandroid.myapplication.LinearGradientSpan
import com.cookandroid.myapplication.R
import com.cookandroid.myapplication.databinding.ActivityRegisterBinding
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 계정 생성 글자 그라데이션
        val txtHello: TextView =binding.text
        val text = " Create \n New Account"
        val start = ContextCompat.getColor(this, R.color.start)
        val end = ContextCompat.getColor(this, R.color.end)
        val spannable = text.toSpannable()
        spannable[0..text.length] = LinearGradientSpan(text, text, start, end)
        txtHello.text = spannable
        // 반짝임 효과
        YoYo.with(Techniques.FadeIn)
            .duration(600)
            .repeat(1)
            .playOn(binding.text)


//Auth part
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()



 //stream part
        val emailStream = RxTextView.textChanges(binding.registerEmail)
            .skipInitialValue()
            .map{email->
                email.length < 6
            }
        emailStream.subscribe{
            showTextMinimalAlert(it, "Email")
        }

        val pwStream = RxTextView.textChanges(binding.registerPW)
            .skipInitialValue()
            .map{ pw ->
                pw.length < 8
            }
        pwStream.subscribe{
            showTextMinimalAlert(it, "Password")
        }

        val pwConfirmStream = Observable.merge(
            RxTextView.textChanges(binding.registerPW)
                .skipInitialValue()
                .map { pw ->
                    pw.toString() != binding.confirmPW.text.toString()
                },
            RxTextView.textChanges(binding.confirmPW)
                .skipInitialValue()
                .map{confirmPW ->
                    confirmPW.toString() != binding.registerPW.text.toString()
                })
        pwConfirmStream.subscribe{
            showPasswordConfirmAlert(it)
        }

///Button binding

        binding.registerSubmitBtn.setOnClickListener{
            val email = binding.registerEmail.text.toString().trim()
            val pw = binding.registerPW.text.toString().trim()
            registerUser(email, pw)
        }
        ///버튼 초기 상태 = 이용불가, 회색
        binding.registerSubmitBtn.isEnabled = false
        binding.registerSubmitBtn.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.grey)

///레지스터 버튼 제어

        val invalidFieldsStream = io.reactivex.Observable.combineLatest(
            emailStream,
            pwStream,
            pwConfirmStream,
            {emailInvalid: Boolean, pwInvalid: Boolean, pwConfirmInvalid: Boolean->
                !emailInvalid && !pwInvalid && !pwConfirmInvalid
            })

        //inValid = true면 버튼 사용 가능, 파란색
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                binding.registerSubmitBtn.isEnabled = true
                binding.registerSubmitBtn.setBackgroundResource(R.drawable.btn_login_ripple)
            }
        }


    }

///Alert part

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String){
        if(text == "Email")
            binding.registerEmail.error = if(isNotValid) "이메일 형식이 틀립니다" else null
        else if (text=="Password")
            binding.registerPW.error = if(isNotValid) "8자 이상이어여야 합니다" else null
    }

    private fun showPasswordConfirmAlert(isNotValid: Boolean){
        binding.confirmPW.error = if(isNotValid) "비밀번호가 일치하지 않습니다" else null
    }

    private fun registerUser(email: String, pw: String){
        auth.createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    startActivity(Intent(this, LoginActivity::class.java))
                    Toast.makeText(this, "계정이 생성되었습니다", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

}