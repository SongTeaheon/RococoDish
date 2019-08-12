package com.rococodish.front_ui.DataModel

class StorePostInfo(val postImagePath : String?,
                    val likeNum : String,
                    val starNum : String,
                    val postingInfo: PostingInfo,
                    val storeInfo: StoreInfo) {
    constructor():this(null, "0", "0.0", PostingInfo(), StoreInfo())
}