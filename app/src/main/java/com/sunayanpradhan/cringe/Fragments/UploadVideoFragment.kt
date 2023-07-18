package com.sunayanpradhan.cringe.Fragments

import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.JsonObject
import com.otaliastudios.autocomplete.Autocomplete
import com.otaliastudios.autocomplete.AutocompleteCallback
import com.otaliastudios.autocomplete.AutocompletePolicy
import com.otaliastudios.autocomplete.AutocompletePresenter
import com.otaliastudios.autocomplete.CharPolicy
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.Models.VideoModel
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.Utils.HashPresenter
import com.sunayanpradhan.cringe.Utils.UserPresenter
import com.sunayanpradhan.cringe.databinding.FragmentUploadVideoBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class UploadVideoFragment : Fragment(){

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

                val videoTitle= Date().time.toTimeDateString()

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

        setupMentionsAutocomplete()


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

//    private fun setupHashAutocomplete() {
//        val elevation = 6f
//        val hash: MutableList<String> = ArrayList()
//        hash.add("hash")
//        hash.add("happy")
//        hash.add("enjoy")
//        hash.add("sad")
//        hash.add("party")
//        hash.add("bash")
//        hash.add("color")
//        hash.add("define")
//        val backgroundDrawable: Drawable = ColorDrawable(Color.WHITE)
//        val hashpolicy: AutocompletePolicy = CharPolicy('#') // Look for #hash
//        val hashpresenter: AutocompletePresenter<String> = HashPresenter(this, hash)
//        val hashcallback: AutocompleteCallback<String> = object : AutocompleteCallback<String> {
//            override fun onPopupItemClicked(editable: Editable, item: String): Boolean {
//                // Replace query text with the full name.
//                val range = CharPolicy.getQueryRange(editable) ?: return false
//                val start = range[0]
//                val end = range[1]
//                editable.replace(start, end, item)
//                // This is better done with regexes and a TextWatcher, due to what happens when
//                // the user clears some parts of the text. Up to you.
////                editable.setSpan(new StyleSpan(Typeface.BOLD), start, start+replacement.length(),
////                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                Log.d(TAG, "AutoComplete text value---->$editable")
//                return true
//            }
//
//            override fun onPopupVisibilityChanged(shown: Boolean) {}
//        }
//        val hashAutocomplete = Autocomplete.on<String>(binding.videoTitle)
//            .with(elevation)
//            .with(backgroundDrawable)
//            .with(hashpolicy)
//            .with(hashpresenter)
//            .with(hashcallback)
//            .build()
//    }

    private fun setupMentionsAutocomplete() {
        val mentionsUser = JsonObject()
        val elevation = 6f
        val backgroundDrawable: Drawable = ColorDrawable(Color.WHITE)
        val policy: AutocompletePolicy = CharPolicy('@') // Look for @mentions
        val presenter = UserPresenter(requireContext())
        val callback: AutocompleteCallback<UserModel> = object : AutocompleteCallback<UserModel> {
            override fun onPopupItemClicked(editable: Editable, item: UserModel): Boolean {
                // Replace query text with the full name.
                val range = CharPolicy.getQueryRange(editable) ?: return false
                val start = range[0]
                val end = range[1]
                val replacement: String = "<b>"+item.userTag+"</b>"
                editable.replace(start, end, Html.fromHtml(replacement))
                // This is better done with regexes and a TextWatcher, due to what happens when
                // the user clears some parts of the text. Up to you.
                mentionsUser.addProperty("user_" + item.userTag, item.userName)
                return true
            }

            override fun onPopupVisibilityChanged(shown: Boolean) {}
        }
        val mentionsAutocomplete = Autocomplete.on<UserModel>(binding.videoTitle)
            .with(elevation)
            .with(backgroundDrawable)
            .with(policy)
            .with(presenter)
            .with(callback)
            .build()
    }

    private fun Long.toTimeDateString(): String {
        val dateTime = Date(this)
        val format = SimpleDateFormat("dd.MM.yyyy hh:mm a", Locale.getDefault())
        return format.format(dateTime)
    }

}