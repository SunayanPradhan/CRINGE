package com.sunayanpradhan.cringe.Activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.sunayanpradhan.cringe.Activities.UploadActivity.Companion.ACTION_CUSTOM_BROADCAST
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.ActivityPreviewVideoBinding
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener

class PreviewVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewVideoBinding


    private val customReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_CUSTOM_BROADCAST) {

                Toast.makeText(this@PreviewVideoActivity, "Finish", Toast.LENGTH_SHORT).show()

                finish()

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_video)

        binding= DataBindingUtil.setContentView(this,R.layout.activity_preview_video)

        val videoUri= intent.getStringExtra("videoUri")

        binding.previewVideo.setMaxDuration(60)

        binding.previewVideo.setVideoURI(Uri.parse(videoUri))

        val filter = IntentFilter(ACTION_CUSTOM_BROADCAST)
        registerReceiver(customReceiver, filter)

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


    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ACTION_CUSTOM_BROADCAST)
        registerReceiver(customReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(customReceiver)
    }


}