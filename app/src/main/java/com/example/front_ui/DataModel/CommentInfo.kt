package com.example.front_ui.DataModel

import java.sql.Timestamp

class CommentInfo(val commentWriterId : String,
                  val writerName : String,
                  val imgPath : String?,
                  val comment : String,
                  val time : Long) {
    constructor(): this("","", null, "",0)
}