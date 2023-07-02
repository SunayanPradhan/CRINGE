package com.sunayanpradhan.cringe.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sunayanpradhan.cringe.Activities.UploadActivity
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.Models.VideoModel
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.FragmentUploadVideoBinding
import java.text.SimpleDateFormat
import java.util.Date


class UploadVideoFragment : Fragment() {

    private lateinit var binding: FragmentUploadVideoBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var storageReference: StorageReference


    private var isOptionOpen= false

    private var videoType= true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentUploadVideoBinding.bind(view)

        firebaseAuth= FirebaseAuth.getInstance()

        firebaseDatabase= FirebaseDatabase.getInstance()

        storageReference= FirebaseStorage.getInstance().reference.child("posts")
            .child(FirebaseAuth.getInstance().uid.toString())
            .child(Date().time.toString())

        setUserData()

        val videoUri = arguments?.getString("videoUri")

        Glide.with(requireContext().applicationContext).load(Uri.parse(videoUri)).into(binding.videoThumbnail)

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



            }
            else{

                val videoTitle= binding.videoTitle.text.toString()

                uploadVideo(Uri.parse(videoUri),videoTitle,videoType)


            }



        }

        binding.uploadBack.setOnClickListener {

            activity?.finish()

        }


    }


    private fun uploadVideo(videoUri: Uri, videoTitle: String, videoType: Boolean) {

        val dialogView = View.inflate(requireContext(), R.layout.custom_progress_dialog_layout, null)

        val builder = AlertDialog.Builder(requireContext()).setView(dialogView).create()

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

                    Toast.makeText(requireContext(), "Upload Successful", Toast.LENGTH_SHORT).show()

                    activity?.finish()


                }



            }



        }.addOnFailureListener {

            Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show()

        }




    }


    private fun setUserData(){

        firebaseDatabase.reference.child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){

                        for (dataSnapshot in snapshot.children){

                            val data= dataSnapshot.getValue(UserModel::class.java)

                            if (data?.userId==firebaseAuth.currentUser?.uid){

                                binding.userTag.text=data?.userTag

                                binding.userName.text=data?.userName

                                Glide.with(requireContext().applicationContext)
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