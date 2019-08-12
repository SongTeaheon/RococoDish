package com.example.front_ui.DataModel

class CouponInfo(val title : String,
                 val toDate : String,
                 val fromDate : String,
                 val atLeastPrice : String,
                 val discountMoney : String) {
    constructor(): this("", "", "", "", "")
}