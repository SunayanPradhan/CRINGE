package com.sunayanpradhan.cringe.Models

data class VideoModel(var videoId: String,
                      var videoTitle: String,
                      var videoUrl: String,
                      var videoStorageId:String,
                      var videoTime: Long,
                      var videoType: Boolean,
                      var videoUserId: String)
{
    constructor():this(
        "",
        "",
        "",
        "",
        0,
        true,
        ""
    )

}

