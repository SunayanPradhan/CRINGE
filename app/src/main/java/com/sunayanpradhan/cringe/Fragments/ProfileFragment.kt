package com.sunayanpradhan.cringe.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.cringe.Adapters.CringeThumbnailAdapter
import com.sunayanpradhan.cringe.Models.SavedModel
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.Models.VideoModel
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.FragmentProfileBinding
import com.sunayanpradhan.deverse.io.Models.FollowerModel
import com.sunayanpradhan.deverse.io.Models.FollowingModel


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var list: ArrayList<VideoModel>

    private lateinit var followersList: ArrayList<FollowerModel>

    private lateinit var followingsList: ArrayList<FollowingModel>

    private lateinit var savedList: ArrayList<SavedModel>

    private lateinit var adapter: CringeThumbnailAdapter

    private lateinit var layoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentProfileBinding.bind(view)

        firebaseDatabase= FirebaseDatabase.getInstance()

        firebaseAuth= FirebaseAuth.getInstance()

        list= ArrayList()

        followersList= ArrayList()

        followingsList= ArrayList()

        savedList= ArrayList()

        adapter= CringeThumbnailAdapter(list,requireContext())

        layoutManager = GridLayoutManager(requireContext(), 3)

//        val dividerItemDecoration= DividerItemDecoration(requireContext(), GridLayoutManager.DEFAULT_SPAN_COUNT)
//
//        binding.profileRecyclerView.addItemDecoration(dividerItemDecoration)

        binding.profileRecyclerView.layoutManager= layoutManager

        binding.profileRecyclerView.adapter= adapter


        setData()

        followersSetData()

        followingsSetData()

        savedSetData()

        binding.pageRefresh.setOnRefreshListener {

            setData()

            followersSetData()

            followingsSetData()

            binding.pageRefresh.isRefreshing= false

        }




    }

    private fun setData(){

        firebaseDatabase.reference.child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){

                        for (dataSnapshot in snapshot.children){

                            val data= dataSnapshot.getValue(UserModel::class.java)

                            if (data?.userId==firebaseAuth.currentUser?.uid){

                                binding.profileId.text="@"+data?.userTag

                                binding.profileProfileName.text=data?.userName

                                Glide.with(context?.applicationContext!!)
                                    .load(data?.userProfilePhoto)
                                    .into(binding.profileProfile)



                            }


                        }



                    }


                }

                override fun onCancelled(error: DatabaseError) {



                }


            })

        firebaseDatabase.reference.child("videos").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()

                for (dataSnapshot in snapshot.children){

                    val data = dataSnapshot.getValue(VideoModel::class.java)

                    if (data?.videoUserId==firebaseAuth.uid.toString()) {

                        list.add(data)

                    }

                }

                list.reverse()

                adapter.notifyDataSetChanged()

                binding.videos.text= list.size.toString()

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })



    }

    private fun followersSetData(){

        firebaseDatabase.reference.child("users")
            .child(firebaseAuth.uid.toString()).child("followers")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    followersList.clear()

                    for(dataSnapshot in snapshot.children){

                        val data= dataSnapshot.getValue(FollowerModel::class.java)

                        followersList.add(data!!)

                    }

                    binding.followers.text= followersList.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {

                }


            })

        binding.followerLogo.setOnClickListener {

           showFollowerDialog()

        }

        binding.followers.setOnClickListener {

            showFollowerDialog()

        }


    }

    private fun followingsSetData(){

        firebaseDatabase.reference.child("users")
            .child(firebaseAuth.uid.toString()).child("followings")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    followingsList.clear()

                    for(dataSnapshot in snapshot.children){

                        val data= dataSnapshot.getValue(FollowingModel::class.java)

                        followingsList.add(data!!)

                    }

                    binding.following.text= followingsList.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {

                }


            })

        binding.followingLogo.setOnClickListener {

            showFollowingDialog()

        }

        binding.following.setOnClickListener {

            showFollowingDialog()

        }

    }

    private fun savedSetData(){

        firebaseDatabase.reference.child("users")
            .child(firebaseAuth.uid.toString()).child("saved")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    savedList.clear()

                    for (dataSnapshot in snapshot.children){

                        val data= dataSnapshot.getValue(SavedModel::class.java)

                        savedList.add(data!!)

                    }

                    binding.saved.text= savedList.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {

                }


            })


        binding.savedLogo.setOnClickListener {

            showSavedDialog()

        }

        binding.saved.setOnClickListener {


            showSavedDialog()

        }


    }

    private fun showFollowerDialog(){



    }

    private fun showFollowingDialog(){



    }


    private fun showSavedDialog(){



    }






}