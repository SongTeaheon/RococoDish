package com.rococodish.front_ui.DataModel

class CouponInfo(val docId: String,
                 val couponDesc : String,
                 val isCouponUsed : Boolean,
                 val toDate : String,
                 val fromDate : String,
                 val storeId : String,
                 val storeName : String) {
    constructor(): this("", "",false,  "", "", "", "")
}