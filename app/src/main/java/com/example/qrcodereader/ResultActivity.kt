package com.example.qrcodereader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.qrcodereader.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val result = intent.getStringExtra("msg") ?: "데이터가 존재하지 않음."

        setUI(result)
    }

    private fun setUI(result: String) {
        //넘어온 QR코드 속 데이터를 텍스트뷰에 설정.
        binding.tvContent.text = result
        binding.btnGoBack.setOnClickListener {
            Log.d(TAG, "Btn go back!")
            finish()    //버튼을 누르면 result activity를 종료.
        }
    }

    companion object {
        val TAG : String = ResultActivity::class.java.simpleName
    }
}