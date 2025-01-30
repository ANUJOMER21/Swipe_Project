package com.swipe.test_project.Api

import java.io.File

data class Add_Product_detail(
      val product_name: String,
      val product_type: String,
      val price: String,
      val tax:String,
      val image: File,

      )