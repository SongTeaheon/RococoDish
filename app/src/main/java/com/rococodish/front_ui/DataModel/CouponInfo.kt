package com.rococodish.front_ui.DataModel

class CouponInfo(val docId: String,
                 val title : String,
                 val isCouponUsed : Boolean,
                 val toDate : String,
                 val fromDate : String,
                 val atLeastPrice : String,
                 val discountMoney : String) {
    constructor(): this("", "",false,  "", "", "", "")
}