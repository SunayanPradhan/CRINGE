package com.sunayanpradhan.cringe.Models

data class LikeModel(var likeVideoId:String, var likeUserId:String, var likeTime: Long)
{
    constructor():this(
        "",
        "",
        0
    )
}

