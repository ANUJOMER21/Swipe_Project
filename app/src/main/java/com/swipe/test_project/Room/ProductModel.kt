package com.swipe.test_project.Room

import androidx.room.Entity
import androidx.room.PrimaryKey
enum class image_type{
    local,net
}
@Entity(tableName = "Product_table")
data class ProductModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val product_image:String,
    val product_price:Double,
    val product_name:String,
    val product_type:String,
    val tax:Double,
    val image_type:image_type,
    val uploaded:Boolean,

)