package com.example.qrcodereader

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QRCodeAnalyzer: ImageAnalysis.Analyzer {
    //바코드 스캐닝 객체 생성.
    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if(mediaImage != null) {
            //이미지가 찍힐 당시의 카메라의 회전 각도를 고려하여 입력 이미지를 생성 한다.
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            //이미지를 분석한다.
            scanner.process(image).addOnSuccessListener {qrCodes ->  //이미지 분석을 성공 했을때

            }.addOnFailureListener {//이미지 분석을 실패 했을때
                it.printStackTrace()
            }.addOnCompleteListener { //이미지 분석을 완료 했을때
                imageProxy.close()
            }
        }
    }
}