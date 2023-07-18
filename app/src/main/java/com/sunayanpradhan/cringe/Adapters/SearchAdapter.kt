package com.sunayanpradhan.cringe.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.Models.VideoModel
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.SearchLayoutBinding
import com.sunayanpradhan.deverse.io.Models.FollowerModel
import com.sunayanpradhan.deverse.io.Models.FollowingModel
import java.util.Date

class SearchAdapter(val list: ArrayList<UserModel>, val context: Context): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var isFollow= false

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var firebaseAuth: FirebaseAuth

    class ViewHolder (val binding: SearchLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view= SearchLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(view)

    }

    override fun getItemCount(): Int {

        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem= list[position]

        firebaseDatabase= FirebaseDatabase.getInstance()

        firebaseAuth= FirebaseAuth.getInstance()

        Glide.with(context.applicationContext)
            .load(currentItem.userProfilePhoto)
            .into(holder.binding.searchProfile)

        holder.binding.searchProfileName.text= currentItem.userName

        holder.binding.searchProfileTag.text= currentItem.userTag


        firebaseDatabase.reference.child("users").child(currentItem.userId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val data= snapshot.getValue(UserModel::class.java)

                if (data?.userId==firebaseAuth.uid){

                    holder.binding.searchFollow.visibility= View.GONE

                }
                else{

                    holder.binding.searchFollow.visibility= View.VISIBLE

                }

            }

            override fun onCancelled(error: DatabaseError) {


            }


        })


        firebaseDatabase.reference
            .child("users")
            .child(firebaseAuth.uid.toString())
            .child("followings")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){

                        for (dataSnapshot in snapshot.children){

                            if (dataSnapshot.key==currentItem.userId){

                                isFollow=true

                                holder.binding.searchFollow.setBackgroundDrawable(
                                    ContextCompat.getDrawable(context,
                                        R.drawable.following_button))
                                holder.binding.searchFollow.text="Following"

                            }


                        }


                    }

                }

                override fun onCancelled(error: DatabaseError) {


                }


            })



        holder.binding.searchFollow.setOnClickListener {

            if (isFollow){

                holder.binding.searchFollow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_button))
                holder.binding.searchFollow.text="Follow"
                isFollow = false

                firebaseDatabase.reference
                    .child("users")
                    .child(currentItem.userId)
                    .child("followers")
                    .child(firebaseAuth.uid.toString())
                    .removeValue()

                firebaseDatabase.reference
                    .child("users")
                    .child(firebaseAuth.uid.toString())
                    .child("followings")
                    .child(currentItem.userId)
                    .removeValue()

            }
            else{

                holder.binding.searchFollow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.following_button))
                holder.binding.searchFollow.text="Following"
                isFollow = true

                firebaseDatabase.reference
                    .child("users")
                    .child(currentItem.userId)
                    .child("followers")
                    .child(firebaseAuth.uid.toString())
                    .setValue(FollowerModel(firebaseAuth.uid.toString(), Date().time))

                firebaseDatabase.reference
                    .child("users")
                    .child(firebaseAuth.uid.toString())
                    .child("followings")
                    .child(currentItem.userId)
                    .setValue(FollowingModel(currentItem.userId, Date().time))

            }

        }


    }
}