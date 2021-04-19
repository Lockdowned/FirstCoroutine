package com.example.coroutineimage

import android.graphics.Bitmap

data class CompressedImg(
    var img: Bitmap? = null,
    var compressGrade: Int? = null
)
