package com.sunayanpradhan.cringe.Activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sunayanpradhan.cringe.R


class MainActivity : AppCompatActivity() {

    private lateinit var bottomBar: BottomNavigationView

    private lateinit var addVideo: CardView

    private lateinit var navController: NavController

    private val VIDEO_REQUEST_CODE=100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomBar= findViewById(R.id.bottom_bar)

        addVideo= findViewById(R.id.add_video)

        supportActionBar?.hide()

        this.theme?.applyStyle(R.style.FullScreen,false)
        this.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)



        val fragments=supportFragmentManager.findFragmentById(R.id.fragments) as NavHostFragment



        navController=fragments.navController


        setupWithNavController(bottomBar,navController)


        addVideo.setOnClickListener {


            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent,VIDEO_REQUEST_CODE)


        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VIDEO_REQUEST_CODE) {

            if (data?.data != null) {
                val videoUri = data.data!!

                val intent= Intent(this,PreviewVideoActivity::class.java)

                intent.putExtra("videoUri",videoUri.toString())

                startActivity(intent)

            } else {


            }


        } else {


        }
    }





}