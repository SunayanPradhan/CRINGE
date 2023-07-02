package com.sunayanpradhan.cringe.Activities

import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sunayanpradhan.cringe.Fragments.UploadVideoFragment
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.ActivityPreviewUploadBinding
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener

class PreviewUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_upload)

        binding= DataBindingUtil.setContentView(this,R.layout.activity_preview_upload)

        val videoUri= intent.getStringExtra("videoUri")

        binding.previewVideo.setMaxDuration(60)

        binding.previewVideo.setVideoURI(Uri.parse(videoUri))


        binding.previewVideo.setOnTrimVideoListener(object : OnTrimVideoListener {
            override fun getResult(uri: Uri?) {

//                val intent= Intent(this@PreviewUploadActivity,UploadActivity::class.java)
//
//                intent.putExtra("videoUri",uri.toString())
//
//                startActivity(intent)

                val fragmentManager:FragmentManager= supportFragmentManager

                val fragmentTransaction:FragmentTransaction= fragmentManager.beginTransaction()

                val bundle= Bundle()

                bundle.putString("videoUri",uri.toString())

                val uploadVideoFragment= UploadVideoFragment()

                uploadVideoFragment.arguments= bundle

                fragmentTransaction.replace(R.id.container,uploadVideoFragment).commit()


            }

            override fun cancelAction() {



            }

        })




        binding.previewBack.setOnClickListener {

            finish()

        }


    }


}