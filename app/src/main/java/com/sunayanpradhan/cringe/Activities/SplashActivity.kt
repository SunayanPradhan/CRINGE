package com.sunayanpradhan.cringe.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.sunayanpradhan.cringe.Models.UserModel
import com.sunayanpradhan.cringe.R
import com.sunayanpradhan.cringe.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val RC_SIGN_IN=100
    private lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        binding= DataBindingUtil.setContentView(this,R.layout.activity_splash)

        firebaseAuth= FirebaseAuth.getInstance()

        checkUser()

        val googleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("899044135905-04o84omap716jjp6r21jdpro0bb29vm1.apps.googleusercontent.com")
            .requestEmail()
            .build()


        googleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions)

        binding.termsConditions.setOnClickListener {

            val openUrlIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://iamtechnosp.blogspot.com/2022/07/cloudy-notes-terms-conditions.html"))
            startActivity(openUrlIntent)

        }

        binding.googleSignIn.setOnClickListener {

            if (binding.checkBox.isChecked) {

                val intent = googleSignInClient.signInIntent
                startActivityForResult(intent, RC_SIGN_IN)
            }


            else{
                Toast.makeText(this, "Accept Terms & Conditions First", Toast.LENGTH_SHORT).show()
            }

        }


        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {

                    handleFacebookAccessToken(result.accessToken)

//                    val request = GraphRequest.newMeRequest(
//                        result.accessToken
//                    ) { `object`, response ->
//                        // Application code
//                        val id=`object`?.getString("id")
//                        val email=`object`?.getString("email")
//                        val fullName= `object`?.getString("name")
//                        val profileUrl=`object`?.getJSONObject("picture")
//                            ?.getJSONObject("data")?.getString("url")
//
////
////                        fbName.text=fullName
////
////                        fbId.text=id
////
////                        fbEmail.text=email
////
////                        Glide.with(applicationContext).load(profileUrl).into(fbImage)
////
//
//                    }
//
//                    val parameters = Bundle()
//                    parameters.putString("fields", "id,name,link,picture.type(large),email")
//                    request.parameters = parameters
//                    request.executeAsync()
//
//
//                    startActivity(Intent(
//                        this@SplashActivity,MainActivity::class.java)
//                    )
//
//                    overridePendingTransition(0,0)
//
//                    finish()
                }

                override fun onCancel() {
                    // App code

                    Toast.makeText(this@SplashActivity,"SignIn Unsuccessful", Toast.LENGTH_SHORT).show()

                }

                override fun onError(exception: FacebookException) {
                    // App code

                    Toast.makeText(this@SplashActivity,"SignIn Unsuccessful", Toast.LENGTH_SHORT).show()


                }
            })

        binding.facebookSignIn.setOnClickListener {

            if (binding.checkBox.isChecked) {

                LoginManager.getInstance()
                    .logInWithReadPermissions(this, listOf("public_profile,email"))


            }


            else{
                Toast.makeText(this, "Accept Terms & Conditions First", Toast.LENGTH_SHORT).show()
            }

        }


    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode,resultCode,data)

        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            RC_SIGN_IN -> {
                val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account=accountTask.getResult(ApiException::class.java)
                    firebaseAuthWithGoogleAccount(account)
                }
                catch (e:Exception){

                    Toast.makeText(this,"SignIn Unsuccessful", Toast.LENGTH_SHORT).show()

                }

            }
        }
    }


    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {

        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)

        firebaseAuth= FirebaseAuth.getInstance()

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {

                val firebaseUser = firebaseAuth.currentUser


                if (it.additionalUserInfo!!.isNewUser){

                    val user = UserModel(firebaseUser?.uid.toString(),firebaseUser?.uid.toString(),firebaseUser?.displayName.toString(),firebaseUser?.photoUrl.toString(),"GOOGLE",false)

                    if (firebaseUser != null) {
                        FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser.uid)
                            .setValue(user).addOnSuccessListener {

                                Toast.makeText(
                                    this,
                                    "Registration Successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }


                    }

                    else{

                        Toast.makeText(this, "User is not found", Toast.LENGTH_SHORT).show()

                    }

                }

                else{

                    Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show()

                }

                startActivity(Intent(this,MainActivity::class.java))

                overridePendingTransition(0,0)

                finish()


            }
            .addOnFailureListener {

                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener(this) {

                val firebaseUser = firebaseAuth.currentUser

                if (it.additionalUserInfo!!.isNewUser) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = UserModel(firebaseUser?.uid.toString(),firebaseUser?.uid.toString(),firebaseUser?.displayName.toString(),firebaseUser?.photoUrl.toString(),"FACEBOOK",false)

                    if (firebaseUser != null) {
                        FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser.uid)
                            .setValue(user).addOnSuccessListener {

                                Toast.makeText(
                                    this,
                                    "Registration Successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                                startActivity(Intent(this, MainActivity::class.java))

                                overridePendingTransition(0,0)

                                finish()

                            }

                    }

                    else{

                        Toast.makeText(this, "User is not found", Toast.LENGTH_SHORT).show()

                    }


                } else {

                    Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show()


                }
            }
    }



    private fun checkUser(){

        val firebaseUser=firebaseAuth.currentUser

        val accessToken= AccessToken.getCurrentAccessToken()


        if (firebaseUser!=null && firebaseUser.isEmailVerified){

            startActivity(Intent(this, MainActivity::class.java))

            overridePendingTransition(0,0)

            finish()
        }

        else if (accessToken!=null && !accessToken.isExpired){

            startActivity(Intent(this, MainActivity::class.java))

            overridePendingTransition(0,0)

            finish()

        }

        else{

            try {
                val googleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("899044135905-04o84omap716jjp6r21jdpro0bb29vm1.apps.googleusercontent.com")
                    .requestEmail()
                    .build()


                val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions)

                Auth.GoogleSignInApi.signOut(googleSignInClient.asGoogleApiClient())

            }
            catch (e:Exception){


            }

            LoginManager.getInstance().logOut()

            firebaseAuth.signOut()

        }

    }
}