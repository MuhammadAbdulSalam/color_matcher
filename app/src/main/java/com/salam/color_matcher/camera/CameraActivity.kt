package com.salam.color_matcher.camera

import android.content.Intent
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.salam.color_matcher.R
import com.salam.color_matcher.databinding.ActivityCameraBinding
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class CameraActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCameraBinding
    private var mCamera: Camera? = null
    private var mPreview: CameraSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mCamera = getCameraInstance()

        mPreview = mCamera?.let {
            CameraSurfaceView(this, it)
        }

        mPreview?.also {
            binding.cameraPreview.addView(it)
        }

        binding.buttonCapture.setOnClickListener {
            // get an image from the camera
            mCamera?.takePicture(null, null, mPicture)
        }

    }

    fun getCameraInstance(): Camera? {
        return try {
            Camera.open()
        } catch (e: Exception) {
            null
        }
    }

    private val mPicture = Camera.PictureCallback { data, _ ->

        mCamera?.release()

        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        Palette.from(bitmap).generate { palette ->
            palette?.let {
                val color = palette.getDominantColor(ContextCompat.getColor(this, R.color.purple_200))
                val returnIntent = Intent()
                returnIntent.putExtra("result", color)
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
    }


    /** Create a file Uri for saving an image or video */
    private fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    /** Create a File for saving an image or video */
    private fun getOutputMediaFile(type: Int): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyCameraApp"
        )
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory")
                    return null
                }
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return when (type) {
            MEDIA_TYPE_IMAGE -> {
                File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
            }
            MEDIA_TYPE_VIDEO -> {
                File("${mediaStorageDir.path}${File.separator}VID_$timeStamp.mp4")
            }
            else -> null
        }
    }
}