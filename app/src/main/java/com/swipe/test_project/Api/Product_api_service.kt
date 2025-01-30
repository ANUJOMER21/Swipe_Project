package com.swipe.test_project.Api

import com.swipe.test_project.Room.ProductModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Product_api_service {
    @GET("get")
    suspend fun getProducts(): Response<List<ProductDetails>>
    @Multipart
    @POST("add")
    suspend fun addProduct(
        @Part("product_name") product_name: RequestBody,
        @Part("product_type") product_type: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part product_image: MultipartBody.Part
    ):  Response<add_product_response>


}