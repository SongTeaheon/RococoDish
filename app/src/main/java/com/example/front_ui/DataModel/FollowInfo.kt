package com.example.front_ui.DataModel

class FollowInfo(val profileImagePath: String?,
                 val profileTextUpper : String,
                 val profileTextLower : String) {
    constructor():this(null, "", "")
}