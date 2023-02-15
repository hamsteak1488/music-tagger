package com.cookandroid.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.cookandroid.myapplication.databinding.ActivityRegisterBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ///stream part

        val idStream = RxTextView.textChanges(binding.registerID)
            .skipInitialValue()
            .map{id->
                id.length < 6
            }
        idStream.subscribe{
            showTextMinimalAlert(it, "ID")
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
            startActivity(Intent(this, LoginActivity::class.java))
        }
        ///버튼 초기 상태 = 이용불가, 회색
        binding.registerSubmitBtn.isEnabled = false
        binding.registerSubmitBtn.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.gray)

///레지스터 버튼 제어

        val invalidFieldsStream = io.reactivex.Observable.combineLatest(
            idStream,
            pwStream,
            pwConfirmStream,
            {idInvalid: Boolean, pwInvalid: Boolean, pwConfirmInvalid: Boolean->
                !idInvalid && !pwInvalid && !pwConfirmInvalid
            })

        //inValid = true면 버튼 사용 가능, 파란색
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                binding.registerSubmitBtn.isEnabled = true
                binding.registerSubmitBtn.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.deepBlue)
            }
        }


    }

///Alert part

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String){
        if(text == "ID")
            binding.registerID.error = if(isNotValid) "6자 이상이여야 합니다" else null
        else if (text=="Password")
            binding.registerPW.error = if(isNotValid) "8자 이상이어여야 합니다" else null
    }

    private fun showPasswordConfirmAlert(isNotValid: Boolean){
        binding.confirmPW.error = if(isNotValid) "비밀번호가 일치하지 않습니다" else null
    }

}