package com.sunayanpradhan.cringe.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.cringe.Adapters.CringeAdapter
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.Models.VideoModel
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.FragmentFollowFeedBinding
import com.sunayanpradhan.deverse.io.Models.FollowingModel


class FollowFeedFragment : Fragment() {

    private lateinit var binding: FragmentFollowFeedBinding

    private lateinit var list: ArrayList<VideoModel>

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follow_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentFollowFeedBinding.bind(view)

        list= ArrayList()

        firebaseDatabase= FirebaseDatabase.getInstance()

        firebaseAuth= FirebaseAuth.getInstance()

        val adapter= CringeAdapter(list,requireContext())

        binding.ffViewPager.adapter= adapter

        firebaseDatabase.reference.child("videos").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()

                for (dataSnapshot in snapshot.children){

                    if (dataSnapshot!=null){

                        val data= dataSnapshot.getValue(VideoModel::class.java)

                        firebaseDatabase.reference.child("users")
                            .child(firebaseAuth.uid.toString())
                            .child("followings").addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onDataChange(userSnapshot: DataSnapshot) {

                                    for (userDataSnapshot in userSnapshot.children){

                                        val userData= userDataSnapshot.getValue(FollowingModel::class.java)

                                        if (userData?.followingTo==data?.videoUserId){

                                            list.add(data!!)
                                            
                                        }


                                    }

                                    list.reverse()

                                    adapter.notifyDataSetChanged()

                                }

                                override fun onCancelled(error: DatabaseError) {



                                }


                            })



                    }

                }



            }

            override fun onCancelled(error: DatabaseError) {



            }


        })


        binding.ffRefresh.setOnRefreshListener {

            firebaseDatabase.reference.child("videos").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    list.clear()

                    for (dataSnapshot in snapshot.children){

                        if (dataSnapshot!=null){

                            val data= dataSnapshot.getValue(VideoModel::class.java)

                            firebaseDatabase.reference.child("users")
                                .child(firebaseAuth.uid.toString())
                                .child("followings").addListenerForSingleValueEvent(object :ValueEventListener{
                                    override fun onDataChange(userSnapshot: DataSnapshot) {

                                        for (userDataSnapshot in userSnapshot.children){

                                            val userData= userDataSnapshot.getValue(FollowingModel::class.java)

                                            if (userData?.followingTo==data?.videoUserId){

                                                list.add(data!!)


                                            }


                                        }

                                        list.reverse()

                                        adapter.notifyDataSetChanged()

                                    }

                                    override fun onCancelled(error: DatabaseError) {



                                    }


                                })



                        }

                    }



                }

                override fun onCancelled(error: DatabaseError) {



                }


            })

            binding.ffRefresh.isRefreshing= false

        }

    }


}