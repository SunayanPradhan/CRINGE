package com.sunayanpradhan.cringe.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.cringe.Models.CommentModel
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.databinding.CommentLayoutBinding

class CommentAdapter(private val list: ArrayList<CommentModel>, var context: Context): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var firebaseAuth: FirebaseAuth

    class ViewHolder(val binding: CommentLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding= CommentLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem= list[position]

        firebaseDatabase= FirebaseDatabase.getInstance()

        firebaseAuth= FirebaseAuth.getInstance()

        firebaseDatabase.reference.child("users")
            .child(currentItem.commentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val data= snapshot.getValue(UserModel::class.java)

                    Glide.with(context).load(data?.userProfilePhoto).into(holder.binding.commentProfile)

                    holder.binding.commentUserName.text= data?.userName

                }

                override fun onCancelled(error: DatabaseError) {


                }


            })

        holder.binding.comment.text= currentItem.commentContent

        val timeAgo= TimeAgo.using(currentItem.commentTime)

        holder.binding.commentTime.text=timeAgo



    }
}