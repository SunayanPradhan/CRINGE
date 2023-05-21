package com.sunayanpradhan.cringe.Activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.otaliastudios.autocomplete.*
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.ActivityUploadBinding


class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseDatabase: FirebaseDatabase

    private var isOptionOpen= false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload)

        firebaseAuth= FirebaseAuth.getInstance()

        firebaseDatabase= FirebaseDatabase.getInstance()

        val videoUri = intent.getStringExtra("videoUri")

        Glide.with(this.applicationContext).load(Uri.parse(videoUri)).into(binding.videoThumbnail)

        binding.optionSelectedBar.setOnClickListener {

            if (isOptionOpen) {

                binding.openOptionImage.setImageResource(R.drawable.round_keyboard_arrow_down_24)

                binding.optionLayout.visibility = View.GONE

                isOptionOpen = false

            } else {

                binding.openOptionImage.setImageResource(R.drawable.round_keyboard_arrow_up_24)

                binding.optionLayout.visibility = View.VISIBLE

                isOptionOpen = true

            }


        }

        binding.optionRadio.setOnCheckedChangeListener { _, p1 ->

            when (p1) {

                R.id.select_public->{

                    binding.optionSelected.text= "Public"

                }

                R.id.select_private->{

                    binding.optionSelected.text= "Private"

                }

            }
        }

    }





}