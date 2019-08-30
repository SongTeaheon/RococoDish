package com.rococodish.front_ui.DataModel

class NoticeInfo(val docId: String,
                 val senderUid: String,
                 val senderImagePath : String?,
                 val storeName:  String?,
                 val type : String,
                 val desc : String,
                 val time : Long,
                 val postingInfo: PostingInfo?){
    constructor() : this("", "", null, null, "", "", 0, null)
}