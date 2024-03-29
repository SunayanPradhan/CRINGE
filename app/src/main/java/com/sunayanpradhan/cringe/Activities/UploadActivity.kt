package com.sunayanpradhan.cringe.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.Models.VideoModel
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.ActivityUploadBinding
import java.text.SimpleDateFormat
import java.util.Date


class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var storageReference: StorageReference


    private var isOptionOpen= false

    private var videoType= true

    companion object {
        const val ACTION_CUSTOM_BROADCAST = "com.sunayanpradhan.cringe.CUSTOM_ACTION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload)

        firebaseAuth= FirebaseAuth.getInstance()

        firebaseDatabase= FirebaseDatabase.getInstance()

        storageReference= FirebaseStorage.getInstance().reference.child("posts")
            .child(FirebaseAuth.getInstance().uid.toString())
            .child(Date().time.toString())

        setUserData()

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

                    videoType= true

                }

                R.id.select_private->{

                    binding.optionSelected.text= "Private"

                    videoType= false

                }

            }
        }



        binding.uploadVideo.setOnClickListener {


            if (binding.videoTitle.text.isEmpty()){

                val videoTitle= SimpleDateFormat.getInstance().format(Date()).toString()

                uploadVideo(Uri.parse(videoUri),videoTitle,videoType)

                val intent = Intent(ACTION_CUSTOM_BROADCAST)

                sendBroadcast(intent)

            }
            else{

                val videoTitle= binding.videoTitle.text.toString()

                uploadVideo(Uri.parse(videoUri),videoTitle,videoType)

                val intent = Intent(ACTION_CUSTOM_BROADCAST)

                sendBroadcast(intent)

            }



        }





    }

    private fun uploadVideo(videoUri: Uri, videoTitle: String, videoType: Boolean) {

        val dialogView = View.inflate(this, R.layout.custom_progress_dialog_layout, null)

        val builder = AlertDialog.Builder(this).setView(dialogView).create()

        val dialogTxt = dialogView.findViewById(R.id.dialog_txt) as TextView

        val progressBar = dialogView.findViewById(R.id.progress_bar) as ProgressBar

        val progressPercentage = dialogView.findViewById(R.id.progress_percentage)as TextView

        dialogTxt.text="Cringe Uploading..."

        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)

        builder.setCancelable(false)

        builder.show()

        storageReference.putFile(videoUri).addOnProgressListener {

            val progress = (100.0 * it.bytesTransferred / it.totalByteCount)

            progressBar.progress= progress.toInt()

            progressPercentage.text= progress.toInt().toString()+"%"

        }.addOnSuccessListener {

            storageReference.downloadUrl.addOnSuccessListener {

                val fire= FirebaseDatabase.getInstance().reference.child("videos").push()

                val post= VideoModel(
                    fire.key.toString(),
                    videoTitle,
                    it.toString(),
                    it.lastPathSegment.toString(),
                    Date().time,
                    videoType,
                    firebaseAuth.uid.toString())

                fire.setValue(post).addOnSuccessListener {

                    builder.dismiss()

                    Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show()

                    finish()


                }



            }



        }.addOnFailureListener {

            Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()

        }




    }


    private fun setUserData(){

        firebaseDatabase.reference.child("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){

                        for (dataSnapshot in snapshot.children){

                            val data= dataSnapshot.getValue(UserModel::class.java)

                            if (data?.userId==firebaseAuth.currentUser?.uid){

                                binding.userTag.text=data?.userTag

                                binding.userName.text=data?.userName

                                Glide.with(this@UploadActivity.applicationContext)
                                    .load(data?.userProfilePhoto)
                                    .into(binding.userProfile)


                            }


                        }



                    }


                }

                override fun onCancelled(error: DatabaseError) {



                }


            })


    }




}