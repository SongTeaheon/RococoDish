package com.example.front_ui.DataModel

class StorePostInfo(val postImagePath : String?,
                    val likeNum : String,
                    val starNum : String) {
    constructor():this(null, "0", "0.0")
}