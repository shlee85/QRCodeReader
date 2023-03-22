package com.example.qrcodereader

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QRCodeAnalyzer(val onDetectListener: OnDetectListener): ImageAnalysis.Analyzer {
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
                Log.d(TAG, "Scanner is Successed")
                for(qrcode in qrCodes) {
                    onDetectListener.onDetect(qrcode.rawValue ?: "")
                }
            }.addOnFailureListener {//이미지 분석을 실패 했을때
                Log.d(TAG, "Scanner is Fail")
                it.printStackTrace()
            }.addOnCompleteListener { //이미지 분석을 완료 했을때
                Log.d(TAG, "Scanner is Complete")
                imageProxy.close()
            }
        }
    }

    companion object {
        val TAG: String = QRCodeAnalyzer::class.java.simpleName
    }
}