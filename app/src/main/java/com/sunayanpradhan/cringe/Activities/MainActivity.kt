package com.sunayanpradhan.cringe.Activities

import android.Manifest
import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.core.persistence.PersistenceManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.AddVideoBarLayoutBinding


class MainActivity : AppCompatActivity() {

    private lateinit var bottomBar: BottomNavigationView

    private lateinit var addVideo: CardView

    private lateinit var navController: NavController

    private val VIDEO_REQUEST_CODE=100

    val VIDEO_CAPTURE_CODE = 101

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

        createFileFolder()

        addVideo.setOnClickListener {


            showAddVideoDialog()


        }



    }

    private fun showAddVideoDialog() {

        val bottomSheetDialog= BottomSheetDialog(this,R.style.AppBottomSheetDialogTheme)

        val bottomSheetView: View = layoutInflater.inflate(R.layout.add_video_bar_layout,null)

        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetDialog.show()

        val bottomSheetBinding = AddVideoBarLayoutBinding.bind(bottomSheetView)

        bottomSheetBinding.addVideoBarClose.setOnClickListener {

            bottomSheetDialog.dismiss()

        }

        bottomSheetBinding.createCringe.setOnClickListener {

            bottomSheetDialog.dismiss()

            if (hasCamera()){

                if (ContextCompat.checkSelfPermission(applicationContext, permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED){


                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    startActivityForResult(intent, VIDEO_CAPTURE_CODE)


                }
                else{

                    Dexter.withContext(this)
                        .withPermissions(
                            permission.CAMERA
                        ).withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                                startActivityForResult(intent, VIDEO_CAPTURE_CODE)
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                permissions: List<PermissionRequest?>?,
                                token: PermissionToken?
                            ) {



                            }
                        }).check()



                    Toast.makeText(this, "CAMERA PERMISSION DENIED", Toast.LENGTH_SHORT).show()



                }


            }
            else{

                Toast.makeText(this, "Camera features are not available", Toast.LENGTH_SHORT).show()

            }


        }

        bottomSheetBinding.uploadCringe.setOnClickListener {

            bottomSheetDialog.dismiss()

            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent,VIDEO_REQUEST_CODE)

            Dexter.withContext(this)
                .withPermission(permission.READ_EXTERNAL_STORAGE)
                .withListener(object :PermissionListener{
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {



                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {




                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {

                    }

                }).check()






        }



    }

    private fun hasCamera(): Boolean {
        return packageManager.hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT
            ) || packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VIDEO_REQUEST_CODE) {

            if (data?.data != null) {
                val videoUri = data.data!!

                val intent= Intent(this,PreviewUploadActivity::class.java)

                intent.putExtra("videoUri",videoUri.toString())

                startActivity(intent)

            } else {


            }


        }

        else if (requestCode == VIDEO_CAPTURE_CODE){

            if (data?.data != null) {

                val videoUri = data.data!!

                val intent= Intent(this,PreviewUploadActivity::class.java)

                intent.putExtra("videoUri",videoUri.toString())

                startActivity(intent)

            } else {


            }

        }

        else {


        }

    }

    private fun createFileFolder() {

        val files = getExternalFilesDirs(Environment.DIRECTORY_MOVIES)

        val folder= files[0]

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){


            if (!folder.exists()){

                folder.mkdirs()

            }


        }

        else{

            if (!folder.exists()){

                folder.mkdirs()

            }
        }
    }



}