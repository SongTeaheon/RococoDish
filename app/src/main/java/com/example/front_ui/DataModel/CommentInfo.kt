package com.example.front_ui.DataModel

import java.sql.Timestamp

class CommentInfo(val imgPath : String,
                  val question : String,
                  val answer : String,
                  val time : Long) {
    constructor(): this("", "", "",0)
}