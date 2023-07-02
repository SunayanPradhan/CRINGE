package com.sunayanpradhan.cringe.Adapters

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.Models.VideoModel
import com.sunayanpradhan.cringe.databinding.CringeLayoutBinding

class CringeAdapter(val list: ArrayList<VideoModel>, val context: Context): RecyclerView.Adapter<CringeAdapter.ViewHolder>() {

    private lateinit var mp:MediaPlayer

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var firebaseAuth: FirebaseAuth

    private var isLike=false

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

        mp = MediaPlayer()

        firebaseDatabase.reference.child("users").child(firebaseAuth.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot!=null){

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


    }


}