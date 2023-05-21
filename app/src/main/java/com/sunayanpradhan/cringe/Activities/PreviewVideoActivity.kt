package com.sunayanpradhan.cringe.Activities

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.ActivityPreviewVideoBinding
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener
import java.io.File
import java.net.URI

class PreviewVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_video)

        binding= DataBindingUtil.setContentView(this,R.layout.activity_preview_video)

        val videoUri= intent.getStringExtra("videoUri")

        binding.previewVideo.setMaxDuration(60)

        binding.previewVideo.setVideoURI(Uri.parse(videoUri))

        binding.previewVideo.setDestinationPath("/storage/emulated/0/DCIM/CRINGE/")

        binding.previewVideo.setOnTrimVideoListener(object : OnTrimVideoListener {
            override fun getResult(uri: Uri?) {

                val intent= Intent(this@PreviewVideoActivity,UploadActivity::class.java)

                intent.putExtra("videoUri",uri.toString())

                startActivity(intent)


            }

            override fun cancelAction() {



            }

        })




        binding.previewBack.setOnClickListener {

            finish()

        }


    }

    private fun getRealPathFromMediaData(data: Uri?): String {
        data ?: return ""

        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(
                data,
                arrayOf(MediaStore.Video.Media.DATA),
                null, null, null
            )

            val col = cursor?.getColumnIndex(MediaStore.Video.Media.DATA)
            cursor?.moveToFirst()

            return col?.let { cursor?.getString(it) }!!
        } finally {
            cursor?.close()
        }
    }


}