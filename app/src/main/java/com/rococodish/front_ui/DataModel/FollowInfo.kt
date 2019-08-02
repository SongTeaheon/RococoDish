package com.rococodish.front_ui.DataModel

class FollowInfo(val profileImagePath: String?,
                 val profileTextUpper : String,
                 val profileTextLower : String,
                 val userUID : String) {
    constructor():this(null, "", "", "")
}