package com.rococodish.front_ui.DataModel

import java.io.Serializable

class CommentInfo(val docUuid : String,
                  val commentWriterId : String,
                  val writerName : String,
                  val imgPath : String?,
                  val comment : String,
                  val time : Long,
                  var isExpanded : Boolean) : Serializable {
    constructor(): this("","","", null, "",0, false)
}