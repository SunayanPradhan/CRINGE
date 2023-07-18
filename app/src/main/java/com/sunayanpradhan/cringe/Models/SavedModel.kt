package com.sunayanpradhan.cringe.Models

data class SavedModel(var savedVideo:String,var savedTime:Long)
{
    constructor():this(
    "",
        0
    )
}
