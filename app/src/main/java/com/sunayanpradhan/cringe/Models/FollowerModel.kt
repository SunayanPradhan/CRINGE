package com.sunayanpradhan.deverse.io.Models

data class FollowerModel(
    var followedBy:String,
    var followedAt:Long,
)
{
    constructor():this(
        "",
        0
    )
}


