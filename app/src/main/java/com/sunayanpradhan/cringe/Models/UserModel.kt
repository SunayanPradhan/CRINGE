package com.sunayanpradhan.cringe.Models

data class UserModel (
    var userId:String,
    var userTag:String,
    var userName:String,
    var userProfilePhoto:String,
    var userType:String,
    var userVerified:Boolean
    )
    {
        constructor():this(
        "",
        "",
        "",
        "",
        "",
        false
        )
    }