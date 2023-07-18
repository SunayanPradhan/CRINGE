package com.sunayanpradhan.cringe.Adapters

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.cringe.Models.CommentModel
import com.sunayanpradhan.cringe.Models.LikeModel
import com.sunayanpradhan.cringe.Models.SavedModel
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.Models.VideoModel
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.AddVideoBarLayoutBinding
import com.sunayanpradhan.cringe.databinding.CommentBottomsheetLayoutBinding
import com.sunayanpradhan.cringe.databinding.CringeLayoutBinding
import com.sunayanpradhan.cringe.databinding.OptionsBottomsheetLayoutBinding
import com.sunayanpradhan.deverse.io.Models.FollowerModel
import com.sunayanpradhan.deverse.io.Models.FollowingModel
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CringeAdapter(private val list: ArrayList<VideoModel>, val context: Context): RecyclerView.Adapter<CringeAdapter.ViewHolder>() {

    private lateinit var mp:MediaPlayer

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var firebaseAuth: FirebaseAuth

    private var isLike=false

    private lateinit var likeList: ArrayList<LikeModel>

    private lateinit var commentList: ArrayList<CommentModel>

    private lateinit var commentAdapter: CommentAdapter

    private var isFollow= false

    class ViewHolder(val binding: CringeLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding= CringeLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem= list[position]

        firebaseDatabase= FirebaseDatabase.getInstance()

        firebaseAuth= FirebaseAuth.getInstance()

        likeList= ArrayList()

        commentList= ArrayList()

        mp = MediaPlayer()

        firebaseDatabase.reference.child("users").child(currentItem.videoUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){

                        val data= snapshot.getValue(UserModel::class.java)

                        holder.binding.cringeProfileName.text= data?.userName

                        Glide.with(context.applicationContext)
                            .load(data?.userProfilePhoto)
                            .into(holder.binding.cringeProfile)

                    }


                }

                override fun onCancelled(error: DatabaseError) {



                }


            })

        firebaseDatabase.reference.child("videos").child(currentItem.videoId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val data= snapshot.getValue(VideoModel::class.java)

                if (data?.videoUserId==firebaseAuth.uid){

                    holder.binding.profileFollow.visibility= View.GONE

                }
                else{

                    holder.binding.profileFollow.visibility= View.VISIBLE

                }

            }

            override fun onCancelled(error: DatabaseError) {


            }


        })

        holder.binding.cringeTitle.text= currentItem.videoTitle

        holder.binding.cringeVideo.setVideoPath(currentItem.videoUrl)

        holder.binding.cringeVideo.setOnPreparedListener {

            holder.binding.cringeShimmerLayout.visibility= View.GONE

            holder.binding.cringeVideoLayout.visibility= View.VISIBLE


            mp= it

            it.start()

        }
        

        holder.binding.cringeVideo.setOnCompletionListener {

            mp= it

            it.start()

        }

        holder.binding.cringeVideoLayout.setOnClickListener {

            if (mp.isPlaying) {

                mp.pause()

                holder.binding.cringeVideoPlay.visibility= View.VISIBLE


            } else {

                mp.start()

                holder.binding.cringeVideoPlay.visibility= View.GONE

            }


        }




        updateLike(holder,currentItem)

        updateComment(holder,currentItem)


        firebaseDatabase.reference.child("videos")
            .child(currentItem.videoId).child("likes")
            .addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (dataSnapshot in snapshot.children){

                        if (dataSnapshot.key.toString()==firebaseAuth.uid.toString()){

                            isLike=true

                            holder.binding.cringeLike.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                R.drawable.ic_round_thumb_up_24_blue,
                                0,
                                0
                            )


                        }


                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {



            }

        })

        firebaseDatabase.reference
            .child("users")
            .child(firebaseAuth.uid.toString())
            .child("followings")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){

                        for (dataSnapshot in snapshot.children){

                            if (dataSnapshot.key==currentItem.videoUserId){

                                isFollow=true

                                holder.binding.profileFollow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.following_button))
                                holder.binding.profileFollow.text="Following"

                            }


                        }


                    }

                }

                override fun onCancelled(error: DatabaseError) {


                }


            })





        holder.binding.cringeLike.setOnClickListener {

            if (isLike){

                isLike=false

                holder.binding.cringeLike.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    R.drawable.ic_round_thumb_up_24,
                    0,
                    0
                )

                firebaseDatabase.reference.child("videos")
                    .child(currentItem.videoId).child("likes")
                    .child(firebaseAuth.uid.toString()).removeValue().addOnSuccessListener {

                        updateLike(holder, currentItem)

                    }


            }
            else{

                isLike= true

                holder.binding.cringeLike.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    R.drawable.ic_round_thumb_up_24_blue,
                    0,
                    0
                )

                firebaseDatabase.reference.child("videos")
                    .child(currentItem.videoId).child("likes")
                    .child(firebaseAuth.uid.toString())
                    .setValue(
                        LikeModel(currentItem.videoId,
                            firebaseAuth.uid.toString(), Date().time)
                    ).addOnSuccessListener {

                        updateLike(holder,currentItem)

                    }

            }


        }

        holder.binding.cringeComment.setOnClickListener {

            showCommentDialog(holder,currentItem)

        }

        holder.binding.cringeOptions.setOnClickListener {

            showOptionDialog(holder,currentItem)

        }


        holder.binding.profileFollow.setOnClickListener {

            if (isFollow){

                holder.binding.profileFollow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_button))
                holder.binding.profileFollow.text="Follow"
                isFollow = false

                firebaseDatabase.reference
                    .child("users")
                    .child(currentItem.videoUserId)
                    .child("followers")
                    .child(firebaseAuth.uid.toString())
                    .removeValue()

                firebaseDatabase.reference
                    .child("users")
                    .child(firebaseAuth.uid.toString())
                    .child("followings")
                    .child(currentItem.videoUserId)
                    .removeValue()

            }
            else{

                holder.binding.profileFollow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.following_button))
                holder.binding.profileFollow.text="Following"
                isFollow = true

                firebaseDatabase.reference
                    .child("users")
                    .child(currentItem.videoUserId)
                    .child("followers")
                    .child(firebaseAuth.uid.toString())
                    .setValue(FollowerModel(firebaseAuth.uid.toString(),Date().time))

                firebaseDatabase.reference
                    .child("users")
                    .child(firebaseAuth.uid.toString())
                    .child("followings")
                    .child(currentItem.videoUserId)
                    .setValue(FollowingModel(currentItem.videoUserId,Date().time))

            }

        }


    }

    private fun updateComment(holder: ViewHolder, currentItem: VideoModel) {

        firebaseDatabase.reference.child("videos")
            .child(currentItem.videoId).child("comments")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    commentList.clear()

                    for (dataSnapshot in snapshot.children){

                        val data= dataSnapshot.getValue(CommentModel::class.java)

                        commentList.add(data!!)

                    }

                    holder.binding.cringeComment.text= commentList.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {

                }


            })

    }

    private fun updateLike(holder: ViewHolder,currentItem: VideoModel){

        firebaseDatabase.reference.child("videos")
            .child(currentItem.videoId).child("likes")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    likeList.clear()

                    for (dataSnapshot in snapshot.children){

                        val data= dataSnapshot.getValue(LikeModel::class.java)

                        likeList.add(data!!)


                    }


                    holder.binding.cringeLike.text=likeList.size.toString()





                }

                override fun onCancelled(error: DatabaseError) {



                }


            })



    }

    private fun showCommentDialog(holder: ViewHolder,currentItem: VideoModel){

        val bottomSheetDialog= BottomSheetDialog(context,R.style.BottomSheetDialogTheme)

        val bottomSheetView: View = LayoutInflater.from(context).inflate(R.layout.comment_bottomsheet_layout,null)

        bottomSheetDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )


        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetDialog.show()




        val bottomSheetBinding = CommentBottomsheetLayoutBinding.bind(bottomSheetView)

        setCommentUserData(bottomSheetBinding,currentItem)

        setVideoData(bottomSheetBinding, currentItem)

        showKeyboard(bottomSheetBinding.commentContent)

        setUpCommentData(bottomSheetBinding,currentItem)

        bottomSheetBinding.commentContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val title: String = bottomSheetBinding.commentContent.text.toString()

                if (title.isNotEmpty()) {

                    bottomSheetBinding.commentPost.visibility = View.VISIBLE

                } else {

                    bottomSheetBinding.commentPost.visibility = View.GONE

                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        bottomSheetBinding.commentPost.setOnClickListener {

            postComment(holder,bottomSheetBinding,currentItem)

        }



    }

    private fun setUpCommentData(
        bottomSheetBinding: CommentBottomsheetLayoutBinding,
        currentItem: VideoModel
    ) {

        val layoutManager=LinearLayoutManager(context)

        bottomSheetBinding.commentRecyclerView.layoutManager= layoutManager

        commentAdapter= CommentAdapter(commentList,context)

        bottomSheetBinding.commentRecyclerView.adapter= commentAdapter

        firebaseDatabase.reference.child("videos").child(currentItem.videoId).child("comments").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                commentList.clear()

                for(dataSnapshot in snapshot.children){

                    val data= dataSnapshot.getValue(CommentModel::class.java)

                    commentList.add(data!!)

                }

                commentList.reverse()

                commentAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {


            }


        })

    }

    private fun setCommentUserData(bottomSheetBinding: CommentBottomsheetLayoutBinding,currentItem: VideoModel) {

        firebaseDatabase.reference.child("users")
            .child(firebaseAuth.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val data= snapshot.getValue(UserModel::class.java)

                    Glide.with(context.applicationContext).load(data?.userProfilePhoto).into(bottomSheetBinding.commentOwnProfile)

                }

                override fun onCancelled(error: DatabaseError) {

                }


            })



    }

    private fun setVideoData(bottomSheetBinding: CommentBottomsheetLayoutBinding,currentItem: VideoModel) {

        bottomSheetBinding.videoTitle.text= currentItem.videoTitle

        bottomSheetBinding.videoDate.text= currentItem.videoTime.toTimeDateString()


        firebaseDatabase.reference.child("users")
            .child(currentItem.videoUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val data= snapshot.getValue(UserModel::class.java)

                    Glide.with(context.applicationContext).load(data?.userProfilePhoto).into(bottomSheetBinding.commentProfile)

                    bottomSheetBinding.commentProfileName.text= data?.userName

                }

                override fun onCancelled(error: DatabaseError) {




                }


            })

    }

    private fun Long.toTimeDateString(): String {
        val dateTime = Date(this)
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(dateTime)
    }

    private fun postComment(
        holder: ViewHolder,
        bottomSheetBinding: CommentBottomsheetLayoutBinding,
        currentItem: VideoModel
    ) {

        val commentId=firebaseDatabase.reference.child("videos").child(currentItem.videoId)
            .child("comments").push().key.toString()

        val data= CommentModel(
            commentId,
            bottomSheetBinding.commentContent.text.toString(),
            firebaseAuth.uid.toString(),
            currentItem.videoId,
            Date().time)

        firebaseDatabase.reference.child("videos")
            .child(currentItem.videoId).child("comments")
            .child(commentId).setValue(data)
            .addOnSuccessListener {

                hideKeyboard(bottomSheetBinding.commentContent)

                bottomSheetBinding.commentContent.setText("")

                updateComment(holder,currentItem)


            }

    }

    private fun showOptionDialog(holder: ViewHolder,currentItem: VideoModel){

        val bottomSheetDialog= BottomSheetDialog(context,R.style.BottomSheetDialogTheme)

        val bottomSheetView: View = LayoutInflater.from(context).inflate(R.layout.options_bottomsheet_layout,null)

        bottomSheetDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )


        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetDialog.show()


        val bottomSheetBinding = OptionsBottomsheetLayoutBinding.bind(bottomSheetView)

        var isSaved=false

        firebaseDatabase.reference.child("users").child(firebaseAuth.uid.toString())
            .child("saved").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (dataSnapshot in snapshot.children){

                        if (dataSnapshot.key==currentItem.videoId){

                            isSaved = true


                        }

                    }

                    if (isSaved){

                        bottomSheetBinding.oblBookmark.setImageResource(R.drawable.ic_round_bookmark_added_24)

                    }
                    else{

                        bottomSheetBinding.oblBookmark.setImageResource(R.drawable.ic_round_bookmark_24)

                    }

                }

                override fun onCancelled(error: DatabaseError) {



                }

            })






        bottomSheetBinding.oblDownloadCard.setOnClickListener {

            Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();

            downloadVideo(currentItem)


            bottomSheetDialog.dismiss()


        }

        bottomSheetBinding.oblBookmarkCard.setOnClickListener {

            if (isSaved){

                bottomSheetBinding.oblBookmark.setImageResource(R.drawable.ic_round_bookmark_24)

                isSaved=false

                firebaseDatabase.reference.child("users").child(firebaseAuth.uid.toString())
                    .child("saved").child(currentItem.videoId).removeValue()


            }
            else{

                bottomSheetBinding.oblBookmark.setImageResource(R.drawable.ic_round_bookmark_added_24)

                isSaved=true

                firebaseDatabase.reference.child("users").child(firebaseAuth.uid.toString())
                    .child("saved").child(currentItem.videoId).setValue(SavedModel(currentItem.videoId,Date().time))


            }


        }

        bottomSheetBinding.oblEditCard.setOnClickListener {




        }

        bottomSheetBinding.oblDeleteCard.setOnClickListener {





        }


    }

    private fun downloadVideo(currentItem: VideoModel) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(currentItem.videoUrl)
        val request = DownloadManager.Request(uri)
        request.setTitle(currentItem.videoTitle + ".mp4")
        request.setDestinationInExternalFilesDir(
            context,
            Environment.DIRECTORY_DOWNLOADS,
            currentItem.videoTitle + ".mp4"
        )
        request.setVisibleInDownloadsUi(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val reference = downloadManager.enqueue(request)
        val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                //Fetching the download id received with the broadcast
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                //Checking if the received broadcast is for our enqueued download by matching download id
                if (reference == id) {
                    Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
                    try {
                        downloadManager.openDownloadedFile(id)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        context.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }


    private fun showKeyboard(comment: EditText?) {

        val manager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        manager.showSoftInput(comment?.rootView, InputMethodManager.SHOW_IMPLICIT)

        comment?.requestFocus()


    }

    private fun hideKeyboard(comment: EditText?) {

        val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        manager.hideSoftInputFromWindow(comment?.applicationWindowToken, 0)


    }



}