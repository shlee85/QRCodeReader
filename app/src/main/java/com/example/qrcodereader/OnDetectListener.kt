package com.example.qrcodereader

interface OnDetectListener {
    //QRcode Analyzer에서 QR코드가 인식되었을 때 호출용도. 이것으로 인하여 Main Activity에서도 알수 있음.
    fun onDetect(msg: String)
}