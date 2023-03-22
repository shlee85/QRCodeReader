package com.example.qrcodereader

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.qrcodereader.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.EmptyCoroutineContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private val PERMISSIONS_REQUEST_CODE = 1
    private val PERMISSIONS_REQUIRED = arrayOf(android.Manifest.permission.CAMERA) //카메라 권한 지정.
    private var isDetected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate!")
        if(!hasPermissions(this)) {
            //카메라 권한 요청
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        } else {
            //권한이 있다면 카메라 시작.
            startCamera()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        isDetected = false
    }

    //권한유무 확인
    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    // 미리보기와 이미지 분석 함수
    fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        Log.d(TAG, "start camera!")
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()

            val preview = getPreview()  //미리보기 객체 가져온다.
            val imageAnalysis = getImageAnalysis()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA //후면 카메라 선택

            //미리보기, 이미지 분석, 이미지 캡쳐중 무엇을 쓸지 정한다. 하나이상 선택 가능. 현재는 미리보기만(preview)설정
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        }, ContextCompat.getMainExecutor(this))
    }

    //미리보기 객체 반환
    fun getPreview() : Preview {
        val preview = Preview.Builder().build() //preview객체 생성
        preview.setSurfaceProvider(binding.barcodePreview.surfaceProvider) //Preview에 Surface를 제공해주는 인터페이스 설정

        return preview
    }

    //권한 요청에 대한 콜백.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSIONS_REQUEST_CODE) {
            if(PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                Toast.makeText(this@MainActivity, "권한 요청이 승인되었습니다.", Toast.LENGTH_SHORT).show()
                startCamera()
            } else {
                Toast.makeText(this@MainActivity, "권한 요청이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun getImageAnalysis() : ImageAnalysis {
        val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
        val imageAnalysis = ImageAnalysis.Builder().build()

        imageAnalysis.setAnalyzer(cameraExecutor,
            QRCodeAnalyzer(object: OnDetectListener {
                override fun onDetect(msg: String) {
                    //Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    if(!isDetected) {
                        isDetected = true

                        val intent = Intent(this@MainActivity, ResultActivity::class.java)
                        intent.putExtra("msg", msg)
                        startActivity(intent)
                    }
                }
            }))

        return imageAnalysis
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }
}