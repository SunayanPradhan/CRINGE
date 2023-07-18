package com.sunayanpradhan.cringe.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.otaliastudios.autocomplete.RecyclerViewPresenter
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.R
import java.util.Locale

class UserPresenter(context: Context) : RecyclerViewPresenter<UserModel?>(context) {
    private var adapter: Adapter? = null

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var all: List<UserModel>

    override fun getPopupDimensions(): PopupDimensions {
        val dims = PopupDimensions()
        dims.width = 600
        dims.height = ViewGroup.LayoutParams.WRAP_CONTENT
        return dims
    }

    override fun instantiateAdapter(): RecyclerView.Adapter<*> {
        adapter = Adapter()
        return adapter!!
    }

    override fun onQuery(query: CharSequence?) {
        var query = query

        firebaseDatabase= FirebaseDatabase.getInstance()

        all= listOf()

        firebaseDatabase.reference.child("users").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                all= snapshot.children.map { dataSnapshot ->

                    val data= dataSnapshot.getValue(UserModel::class.java)


                    data?.let { UserModel(it.userId,data.userTag,data.userName,data.userProfilePhoto,data.userType, data.userVerified) }!!


                }

                if (TextUtils.isEmpty(query)) {
                    adapter!!.setData(all)
                } else {
                    query = query.toString().lowercase(Locale.getDefault())
                    val list: MutableList<UserModel> = ArrayList()
                    for (u in all) {
                        if (u.userTag.toLowerCase().contains(query as String) ||
                            u.userName.toLowerCase().contains(query as String)
                        ) {
                            list.add(u)
                        }
                    }
                    adapter!!.setData(list)
                    Log.e("UserPresenter", "found " + list.size + " users for query " + query)
                }
                adapter!!.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {



            }


        })




    }

    private inner class Adapter : RecyclerView.Adapter<Adapter.Holder>() {
        private var data: List<UserModel>? = null

        private inner class Holder(val root: View) :
            RecyclerView.ViewHolder(root) {
            val userTag: TextView = itemView.findViewById(R.id.user_tag)
            val userName: TextView = itemView.findViewById(R.id.user_name)
            val userProfile: ImageView = itemView.findViewById(R.id.user_profile)

        }

        fun setData(data: List<UserModel>?) {
            this.data = data
        }

        override fun getItemCount(): Int {
            return if (isEmpty) 1 else data!!.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false))
        }

        private val isEmpty: Boolean
            get() = data == null || data!!.isEmpty()

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: Holder, position: Int) {
            if (isEmpty) {
                holder.userName.text = "No user here!"
                holder.userTag.text = "Sorry!"
                holder.root.setOnClickListener(null)
                return
            }
            val user: UserModel = data!![position]
            holder.userName.text = user.userName
            holder.userTag.text = "@" + user.userTag
            Glide.with(context.applicationContext)
                .load(user.userProfilePhoto)
                .into(holder.userProfile)

            holder.root.setOnClickListener { dispatchClick(user) }
        }
    }
}
