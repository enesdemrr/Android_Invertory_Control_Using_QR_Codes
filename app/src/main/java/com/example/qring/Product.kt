package com.example.qring

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Product(
    var product_name: String? = null,
    var product_id: String? = null,
    var product_explanation: String? = null,
    var product_amount: String? = null,
    var product_qrcode: String? = null,
    var product_userid: String? = null
)