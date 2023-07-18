package com.sunayanpradhan.cringe.Models

data class ShareModel(var shareId: String, var shareVideoId: String, var shareUserId:String, var shareTime: Long)
{
    constructor():this(
        "",
        "",
        "",
        0
    )
}
