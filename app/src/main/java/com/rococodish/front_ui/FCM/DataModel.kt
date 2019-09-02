package com.rococodish.front_ui.FCM

class DataModel(val title: String,
                val body: String,
                val click_action: String,
                val goNotice: String) {
    constructor() : this("", "", "", "")
}