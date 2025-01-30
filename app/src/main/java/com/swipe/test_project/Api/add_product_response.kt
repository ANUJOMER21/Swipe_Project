package com.swipe.test_project.Api

data class add_product_response(
    val message: String?,
    val product_details: ProductDetails?,
    val product_id: Int?,
    val success: Boolean
)