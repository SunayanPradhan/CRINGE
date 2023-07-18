package com.sunayanpradhan.cringe.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.cringe.Activities.CringeViewActivity
import com.sunayanpradhan.cringe.Models.CommentModel
import com.sunayanpradhan.cringe.Models.LikeModel
import com.sunayanpradhan.cringe.Models.VideoModel
import com.sunayanpradhan.cringe.databinding.CringeThumbnailLayoutBinding

class CringeThumbnailAdapter(val list: ArrayList<VideoModel>, val context: Context): RecyclerView.Adapter<CringeThumbnailAdapter.ViewHolder>() {

    lateinit var firebaseDatabase: FirebaseDatabase

    lateinit var firebaseAuth: FirebaseAuth

    lateinit var likeList: ArrayList<LikeModel>

    lateinit var commentList: ArrayList<CommentModel>

    class ViewHolder(val binding: CringeThumbnailLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding= CringeThumbnailLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem= list[position]

        Glide.with(context.applicationContext)
            .load(currentItem.videoUrl)
            .into(holder.binding.ctImage)

        firebaseDatabase= FirebaseDatabase.getInstance()

        firebaseAuth= FirebaseAuth.getInstance()

        likeList= ArrayList()

        commentList = ArrayList()

        firebaseDatabase.reference.child("videos").child(currentItem.videoId)
            .child("likes").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    likeList.clear()

                    for (dataSnapshot in snapshot.children){

                        val data= dataSnapshot.getValue(LikeModel::class.java)

                        likeList.add(data!!)
                    }

                    holder.binding.ctLike.text= likeList.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {

                }


            })


        firebaseDatabase.reference.child("videos").child(currentItem.videoId)
            .child("comments").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    commentList.clear()

                    for (dataSnapshot in snapshot.children){

                        val data= dataSnapshot.getValue(CommentModel::class.java)

                        commentList.add(data!!)

                    }

                    holder.binding.ctComment.text= commentList.size.toString()


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        holder.itemView.setOnClickListener {

            val intent= Intent(context, CringeViewActivity::class.java)

            intent.putExtra("videoId",currentItem.videoId)

            context.startActivity(intent)
        }


    }
}