package com.sunayanpradhan.cringe.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.cringe.Adapters.CringeAdapter
import com.sunayanpradhan.cringe.Models.VideoModel
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.FragmentRecommendedFeedBinding

class RecommendedFeedFragment : Fragment() {

    private lateinit var binding: FragmentRecommendedFeedBinding

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var list: ArrayList<VideoModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommended_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentRecommendedFeedBinding.bind(view)

        firebaseDatabase= FirebaseDatabase.getInstance()

        list= ArrayList()

        val adapter= CringeAdapter(list,requireContext())

        binding.rfViewPager.adapter= adapter

        firebaseDatabase.reference.child("videos")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    list.clear()

                    for (dataSnapshot in snapshot.children){

                        val data= dataSnapshot.getValue(VideoModel::class.java)

                        list.add(data!!)

                    }

                    adapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {

                }


            })


    }

}