package com.cookandroid.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.cookandroid.myapplication.PlayMusicActivity.Companion.binding
import com.cookandroid.myapplication.databinding.ActivityLoginBinding
import com.jakewharton.rxbinding2.widget.RxTextView

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//stream part

        //ID란 공백 시 공백 알림 호출
        val idStream = RxTextView.textChanges(binding.userID)
            .skipInitialValue()
            .map{id->
                id.isEmpty()
            }
        idStream.subscribe{
            showTextExistAlert(it, "ID")
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
            startActivity(Intent(this, MainActivity::class.java))
        }
        //버튼 초기 상태 사용 불가, 회색
        binding.loginBtn.isEnabled = false
        binding.loginBtn.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.gray)
        //레지스터 버튼 -> RegisterActivity
        binding.registerBtn.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

//로그인 버튼 사용 제어

        val invalidFieldsStream = io.reactivex.Observable.combineLatest(
            idStream,
            pwStream,
            {idInvalid: Boolean, pwInvalid: Boolean->
                !idInvalid && !pwInvalid
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
        if(text=="ID")
            binding.userID.error = if(isNotValid) "아이디를 입력하세요" else null
        else if(text=="Password")
            binding.userPW.error = if(isNotValid) "비밀번호를 입력하세요" else null
    }
}