package com.sunayanpradhan.cringe.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunayanpradhan.cringe.Adapters.SearchAdapter
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private lateinit var list: ArrayList<UserModel>

    private lateinit var adapter: SearchAdapter

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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchBinding.bind(view)

        firebaseDatabase= FirebaseDatabase.getInstance()

        firebaseAuth= FirebaseAuth.getInstance()

        list= ArrayList()

        adapter= SearchAdapter(list,requireContext())

        val layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

        binding.searchRecyclerview.layoutManager= layoutManager

        binding.searchRecyclerview.adapter= adapter

        firebaseDatabase.reference.child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {


                    if (snapshot.exists()){

                        for (dataSnapshot in snapshot.children){

                            val data= dataSnapshot.getValue(UserModel::class.java)

                            list.add(data!!)


                        }

                        adapter.notifyDataSetChanged()


                    }


                }

                override fun onCancelled(error: DatabaseError) {


                }


            })


    }

}