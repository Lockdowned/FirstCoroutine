package com.example.coroutineimage

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coroutineimage.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val logTag = "WithCoroutine"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectButton.setOnClickListener {
            Intent(Intent.ACTION_OPEN_DOCUMENT).also { // of if prefer Intent.ACTION_PICK(WHY DOWNLOAD ВОПРОС)
                it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//                it.addCategory(Intent.CATEGORY_OPENABLE) // ЧТО ДЕЛАЕТ
                it.type = "image/*"
                regImageLauncher.launch(it)
            }
        }
    }

    private val regImageLauncher = registerForActivityResult(ActivityResultContracts.
    StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.clipData?.let { imgData ->
                val compressedImgList = doCompression(imgData)

                binding.rvImage.adapter = ItemImgAdapter(compressedImgList)
                binding.rvImage.layoutManager = LinearLayoutManager(this)
            }
        }
    }


    /**
     * With ExecutorService
     */
//    private fun doCompression(imgData: ClipData): List<CompressedImg> {
//        val compressedImgList = mutableListOf<CompressedImg>()
//
//        val es: ExecutorService = Executors.newFixedThreadPool(5)
//
//        val imgCount = imgData.itemCount
//        val compressionTime = measureTimeMillis {
//            for (i in 0 until imgCount){
//                val task = Runnable {
//                    val imgUri = imgData.getItemAt(i).uri
//                    Log.d(logTag, "Img $i URI: $imgUri")
//                    compressedImgList.add(CompressedImg(compressImage(imgUri), 50))
//                }
//                es.submit(task)
//            }
//            es.shutdown()
//            es.awaitTermination(1, TimeUnit.MINUTES)
//        }
//
//        Log.d(logTag, "Time executing: $compressionTime, final img count ${compressedImgList.size}")
//        return compressedImgList
//    }

    /**
    With Coroutine
     */
    private fun doCompression(imgData: ClipData): List<CompressedImg> {
        val compressedImgList = mutableListOf<CompressedImg>()

        var job: Job

        val imgCount = imgData.itemCount
        val compressionTime = measureTimeMillis {
            job = lifecycleScope.launch(Dispatchers.Default) {
                val deferredALot: List<Deferred<Int>> = (0 until imgCount).map {
                    async {
                        val imgUri = imgData.getItemAt(it).uri
                        Log.d(logTag, "Img $it URI: $imgUri")
                        compressedImgList.add(CompressedImg(compressImage(imgUri), 50))
                        it
                    }
                }
                deferredALot.awaitAll()
            }
            runBlocking {
                job.join()
            }
        }

        Log.d(logTag, "Time executing: $compressionTime, final img count ${compressedImgList.size}")
        return compressedImgList
    }

//    private fun doCompression(imgData: ClipData): List<CompressedImg> {
//        val compressedImgList = mutableListOf<CompressedImg>()
//
//        var job: Job
//
//        val imgCount = imgData.itemCount
//        val compressionTime = measureTimeMillis {
//            job = lifecycleScope.launch(Dispatchers.Default) {
//                Log.d(logTag,"In First Job  Thread: ${Thread.currentThread().name}")
//                var deferred: Deferred<Boolean>? = null
//                for (i in 0 until imgCount) {
//                    deferred = async {
//                        val imgUri = imgData.getItemAt(i).uri
//                        Log.d(logTag, "Img $i URI: $imgUri")
////                        if (i == imgCount / 2) {
////                            delay(5000) // interesting: why working correctly ВОПРОС
////                        }
//                        compressedImgList.add(CompressedImg(compressImage(imgUri), 50))
//                    }
//                }
//                deferred?.await()
//            }
//            runBlocking {
//                job.join()
//            }
//        }
//
//        Log.d(logTag, "Time executing: $compressionTime, final img count ${compressedImgList.size}")
//        return compressedImgList
//    }

//    private fun doCompression(imgData: ClipData): List<CompressedImg> {
//        val compressedImgList = mutableListOf<CompressedImg>()
//
//        var job: Job
//
//        val imgCount = imgData.itemCount
//        val compressionTime = measureTimeMillis {
//            job = lifecycleScope.launch(Dispatchers.Unconfined) { // why dying nothing Dispatchers.Main ВОПРОС
//                Log.d(logTag,"In First Job Thread: ${Thread.currentThread().name}")
//                val list: MutableList<Job> = mutableListOf()
//                for (i in 0 until imgCount) {
//                    val whatEver = lifecycleScope.launch(Dispatchers.Default) {
//                        val imgUri = imgData.getItemAt(i).uri
//                        Log.d(logTag, "Img $i URI: $imgUri")
//                        compressedImgList.add(CompressedImg(compressImage(imgUri), 50))
//                    }
//                    list.add(whatEver)
//                }
//                runBlocking {
//                    list.forEach(){
//                        it.join()
//                    }
//                }
//            }
//            runBlocking {
//                job.join()
//            }
//        }
//
//        Log.d(logTag, "Time executing: $compressionTime, final img count ${compressedImgList.size}")
//        return compressedImgList
//    }

    /*
    WITHOUT COROUTINE
     */
//    private fun doCompression(imgData: ClipData): List<CompressedImg> {
//        val compressedImgList = mutableListOf<CompressedImg>()
//        val imgCount = imgData.itemCount
//        val compressionTime = measureTimeMillis {
//            for (i in 0 until imgCount) {
//                val imgUri = imgData.getItemAt(i).uri
//                Log.d(logTag, "Img $i URI: $imgUri")
//                compressedImgList.add(CompressedImg(compressImage(imgUri), 50))
//            }
//        }
//        Log.d(logTag, "Time executing: $compressionTime")
//        return compressedImgList
//    }

    private fun compressImage(imgUri: Uri): Bitmap {
        Log.d(logTag, "Executing compressing from Thread: ${Thread.currentThread().name}")
        val imgOptions = BitmapFactory.Options()
        imgOptions.inJustDecodeBounds = true
        val inputStream = applicationContext.contentResolver.openInputStream(imgUri)

        val imageBitmap = BitmapFactory.decodeStream(inputStream)

        var scaledImg = imageBitmap
        Log.d(logTag, "Size before compress ${scaledImg.allocationByteCount}")
        scaledImg = resizeImg(imageBitmap, 0.1f)
        Log.d(logTag, "Size after compress ${scaledImg.allocationByteCount}  $imgUri")



        return scaledImg
    }


    private fun resizeImg(bitmap: Bitmap, scaleFactor: Float): Bitmap{
        val newWidth = bitmap.width * scaleFactor
        val newHeight = bitmap.height * scaleFactor
        val scaledBitmap = Bitmap.createBitmap(newWidth.toInt(), newHeight.toInt(), Bitmap.Config.ARGB_8888)

        val ratioX = newWidth / bitmap.getWidth()
        val ratioY = newHeight / bitmap.getHeight()
        val middleX = newWidth / 2.0f
        val middleY = newHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
                bitmap,
                middleX - bitmap.getWidth() / 2,
                middleY - bitmap.getHeight() / 2,
                Paint(Paint.FILTER_BITMAP_FLAG)
        )
        return scaledBitmap
    }


}
