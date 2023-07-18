package com.sunayanpradhan.cringe.Models

data class CommentModel(
    var commentId: String,
    var commentContent: String,
    var commentUserId: String,
    var commentPostId: String,
    var commentTime: Long)
{
    constructor(): this(
        "",
        "",
        "",
        "",
        0
    )

}
