package com.example.front_ui.DataModel

import java.sql.Timestamp

class CommentInfo(val commentWriterId : String,
                  val imgPath : String?,
                  val question : String,
                  val answer : String?,
                  val time : Long) {
    constructor(): this("","", "", null,0)
}