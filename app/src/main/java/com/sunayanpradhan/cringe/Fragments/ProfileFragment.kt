package com.sunayanpradhan.cringe.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentProfileBinding.bind(view)

        firebaseDatabase= FirebaseDatabase.getInstance()

        firebaseAuth= FirebaseAuth.getInstance()

        setData()

        binding.pageRefresh.setOnRefreshListener {

            setData()

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


    }


}