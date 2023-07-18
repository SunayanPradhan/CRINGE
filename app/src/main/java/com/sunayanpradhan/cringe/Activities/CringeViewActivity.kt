package com.sunayanpradhan.cringe.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.cringe.Adapters.CringeAdapter
import com.sunayanpradhan.cringe.Models.VideoModel
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.ActivityCringeViewBinding

class CringeViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCringeViewBinding

    private lateinit var list: ArrayList<VideoModel>

    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cringe_view)

        binding= DataBindingUtil.setContentView(this, R.layout.activity_cringe_view)

        list = ArrayList()

        firebaseDatabase= FirebaseDatabase.getInstance()

        val videoId = intent.getStringExtra("videoId").toString()

        val adapter= CringeAdapter(list, this)

        binding.cringeViewPager.adapter= adapter

        firebaseDatabase.reference.child("videos").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()

                for (dataSnapshot in snapshot.children){

                    if (dataSnapshot.key.toString()==videoId){

                        val data= dataSnapshot.getValue(VideoModel::class.java)

                        list.add(data!!)
                    }

                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })



    }
}