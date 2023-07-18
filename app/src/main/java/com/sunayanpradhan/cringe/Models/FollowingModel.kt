package com.sunayanpradhan.deverse.io.Models

data class FollowingModel(
    var followingTo:String,
    var followingAt:Long
)
{
    constructor():this(
        "",
        0
    )
}
