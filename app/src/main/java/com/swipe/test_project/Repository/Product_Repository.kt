package com.swipe.test_project.Repository

import android.util.Log
import com.swipe.test_project.Api.Add_Product_detail
import com.swipe.test_project.Api.ApiResponse
import com.swipe.test_project.Api.Product_api_service
import com.swipe.test_project.Api.add_product_response
import com.swipe.test_project.Room.ProductDao
import com.swipe.test_project.Room.ProductModel
import com.swipe.test_project.Room.image_type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
class Product_Repository(
    private val api: Product_api_service,
    private val dao: ProductDao
) {
    private val TAG = "ProductRepository"

    private suspend fun addProductToDb(addProductDetail: Add_Product_detail) {
        withContext(Dispatchers.IO) {
            dao.insert_product(
                ProductModel(
                    product_image = addProductDetail.image.path,
                    product_name = addProductDetail.product_name,
                    product_type = addProductDetail.product_type,
                    product_price = addProductDetail.price.toDoubleOrNull() ?: 0.0,
                    tax = addProductDetail.tax.toDoubleOrNull() ?: 0.0,
                    image_type = image_type.local,
                    uploaded = false
                )
            )
        }
    }

    fun addProduct(
        addProductDetail: Add_Product_detail,
        isNetworkAvailable: Boolean
    ): Flow<ApiResponse<String>> = flow {
        emit(ApiResponse.Loading)

        if (isNetworkAvailable) {
            try {
                val requestFile = addProductDetail.image.asRequestBody("image/*".toMediaTypeOrNull())
                val productImage = MultipartBody.Part.createFormData(
                    "files[]", addProductDetail.image.name, requestFile
                )

                val productName = addProductDetail.product_name.toRequestBody("text/plain".toMediaTypeOrNull())
                val productType = addProductDetail.product_type.toRequestBody("text/plain".toMediaTypeOrNull())
                val price = addProductDetail.price.toRequestBody("text/plain".toMediaTypeOrNull())
                val tax = addProductDetail.tax.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = api.addProduct(
                    product_name = productName,
                    product_type = productType,
                    price = price,
                    tax = tax,
                    product_image = productImage
                )

                if (response.isSuccessful && response.body() != null) {
                    emit(ApiResponse.Success(response.body()!!.message!!))
              //      dao.delete_all_products()
                    syncProductsFromApi()
                } else {
                    emit(ApiResponse.Error(response.message()))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding product: ${e.message}", e)
                emit(ApiResponse.Error("Error: ${e.localizedMessage}"))
            }
        } else {
            addProductToDb(addProductDetail)
            emit(ApiResponse.Success("Product add to Local"))
        }
    }

    fun getProducts(): Flow<List<ProductModel>> = dao.get_all_product()

    suspend fun syncProductsFromApi() {
        withContext(Dispatchers.IO) { // Ensures database operations run on IO thread
            try {
                val response = api.getProducts()
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        for (it in data) {
                            val product = ProductModel(
                                product_image = it.image,
                                product_name = it.product_name,
                                product_type = it.product_type,
                                product_price = it.price,
                                tax = it.tax,
                                image_type = image_type.net,
                                uploaded = true
                            )

                            val existingProduct = dao.findMatchingProduct(
                                name = product.product_name,
                                type = product.product_type,
                                price = product.product_price,
                                tax = product.tax
                            )

                            if (existingProduct != null) {
                                Log.d(TAG, "Existing Product: ${existingProduct.product_name} || ${existingProduct.uploaded}")
                            }
                            if (existingProduct == null) {

                                dao.insert_product(product)
                                Log.d(TAG, "Inserted new product: $product")
                            } else if(!existingProduct.uploaded){
                                Log.d(TAG, "Local Product Deleted new product added: ${product.id}")
                                dao.delete_products(existingProduct.id.toString())
                                dao.insert_product(product)
                                Log.d(TAG, "Inserted new product: $product")
                            }
                            else{


                                Log.d(TAG, "Product already exists: $product")
                            }
                        }
                    } else {
                        Log.d(TAG, "No products found in the response")
                    }
                } else {
                    Log.e(TAG, "Failed to sync products. Response: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing products: ${e.message}", e)
            }
        }
    }


    suspend fun uploadPendingProducts() {
        withContext(Dispatchers.IO) {
            val pendingProducts = dao.get_non_uploaded_product().firstOrNull() ?: emptyList()
            Log.d(TAG, "Pending products count: ${pendingProducts.size}")

            if (pendingProducts.isNotEmpty()) {
                for (product in pendingProducts) {
                    try {
                        val imageFile = File(product.product_image)
                        val response = api.addProduct(
                            product_name = product.product_name.toRequestBody("text/plain".toMediaTypeOrNull()),
                            product_type = product.product_type.toRequestBody("text/plain".toMediaTypeOrNull()),
                            price = product.product_price.toString()
                                .toRequestBody("text/plain".toMediaTypeOrNull()),
                            tax = product.tax.toString()
                                .toRequestBody("text/plain".toMediaTypeOrNull()),
                            product_image = MultipartBody.Part.createFormData(
                                "product_image", imageFile.name,
                                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                            )
                        )

                        if (response.isSuccessful && response.body() != null) {
                            Log.d(
                                TAG,
                                "Uploaded product successfully: ${response.body()!!.message}"
                            )

                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error uploading product: ${e.message}")
                    }
                }
                syncProductsFromApi()
            }
        }
    }
}



/*

class Product_Repository(
    private val api: Product_api_service,
    private val dao: ProductDao
) {
    private val TAG="Product_Repository"
    private suspend fun add_product_todb(
        addProductDetail: Add_Product_detail,
    ){
        dao.insert_product(ProductModel(
            product_image = addProductDetail.image.path,
            product_name = addProductDetail.product_name,
            product_type = addProductDetail.product_type,
            product_price = addProductDetail.price.toDoubleOrNull()?: 0.0,
            tax = addProductDetail.tax.toDoubleOrNull()?:0.0,
            image_type = image_type.local,
            uploaded = false
        ))
    }


    fun addProduct(
        addProductDetail: Add_Product_detail,
        isNetworkAvailable: Boolean
    ): Flow<ApiResponse<String>> = flow {

        emit(ApiResponse.Loading)

        if (isNetworkAvailable) {
            try {

                // Move to IO thread for network calls
                val response = withContext(Dispatchers.IO) {
                    val requestFile = addProductDetail.image.asRequestBody("image/*".toMediaTypeOrNull())
                    val productImage = MultipartBody.Part.createFormData("files[]", addProductDetail.image.name, requestFile)
                    Log.d("Product_Image",addProductDetail.image.path.toString())
                    val productName = RequestBody.create("text/plain".toMediaTypeOrNull(), addProductDetail.product_name)
                    val productType = RequestBody.create("text/plain".toMediaTypeOrNull(), addProductDetail.product_type)
                    val price = RequestBody.create("text/plain".toMediaTypeOrNull(), addProductDetail.price)
                    val tax = RequestBody.create("text/plain".toMediaTypeOrNull(), addProductDetail.tax)

                    api.addProduct(product_name = productName, product_type = productType, price = price, tax = tax, product_image = productImage)
                }
                Log.d("api_response",response.toString())
                if(response.isSuccessful){
                    emit(ApiResponse.Success(response.body()!!.message.toString()))
                    dao.delete_all_products()
                    syncProductsFromApi()
                }
                else{

                    emit(ApiResponse.Error(response.message()))
                }


            } catch (e: Exception) {
                Log.d("error",e.toString())
                emit(ApiResponse.Error(" error: ${e}"))
            }

        } else {
            withContext(Dispatchers.IO) {
                add_product_todb(addProductDetail)
            }

            emit(ApiResponse.Success("Add Locally"))
        }
    }

    suspend fun getProducts(): Flow<List<ProductModel>> = dao.get_all_product()

    suspend fun syncProductsFromApi() {
        try {
            // Upload pending products before fetching new ones

            // Get products from the API
            val response = api.getProducts()
            Log.d(TAG, "syncProductsFromApi response: $response")

            // Check if the response is successful
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    // Iterate over the received data
                    for (it in data) {
                        val product = ProductModel(
                            product_image = it.image,
                            product_name = it.product_name,
                            product_type = it.product_type,
                            product_price = it.price,
                            tax = it.tax,
                            image_type = image_type.net,
                            uploaded = true
                        )

                        // Check and insert product into database
                        val existingProduct = withContext(Dispatchers.IO) {
                            dao.findMatchingProduct(
                                name = product.product_name,
                                type = product.product_type,
                                price = product.product_price,
                                tax = product.tax,

                            )
                        }


                        // Insert product only if not already present
                        if (existingProduct == null) {
                            withContext(Dispatchers.IO) {
                                dao.insert_product(product)
                            }
                            Log.d(TAG, "Inserted new product: $product")
                        } else {
                            Log.d(TAG, "Product already exists: $product")
                        }
                    }
                } else {
                    Log.d(TAG, "No products found in the response")
                }
            } else {
                Log.e(TAG, "Failed to sync products. Response not successful.")
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error syncing products: ${e.message}", e)
        }
    }



    suspend fun uploadPendingProducts() {
        val pendingProducts = dao.get_non_uploaded_product().firstOrNull() ?: emptyList()
        Log.d("pendingProducts",pendingProducts.size.toString())
        if (pendingProducts.isNotEmpty()) {
            for (product in pendingProducts) {
                try {
                    val image= File(product.product_image)
                    val response = api.addProduct(
                        product_name = product.product_name.toRequestBody("text/plain".toMediaTypeOrNull()),
                        product_type = product.product_type.toRequestBody("text/plain".toMediaTypeOrNull()),
                        price = product.product_price.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                        tax = product.tax.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                        product_image = MultipartBody.Part.createFormData(
                            "product_image", image.name,
                           image.asRequestBody("image/*".toMediaTypeOrNull())
                        )
                    )

                    if (response.isSuccessful) {
                        Log.d("uploadPendingProducts",response.body()!!.message.toString())
                        Log.d("product_id",product.id.toString())

                        withContext(Dispatchers.IO){
                            dao.delete_all_products()
                            syncProductsFromApi()

                        }
                            }
                } catch (e: Exception) {
                    Log.e("Repository", "Error uploading product: ${e.message}")
                }
            }
            syncProductsFromApi()
        }
    }







}*/