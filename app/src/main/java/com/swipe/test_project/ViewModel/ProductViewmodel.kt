package com.swipe.test_project.ViewModel

import androidx.lifecycle.ViewModel
import com.swipe.test_project.Repository.Product_Repository


import androidx.lifecycle.viewModelScope
import com.swipe.test_project.Api.Add_Product_detail
import com.swipe.test_project.Api.ApiResponse
import com.swipe.test_project.Room.ProductModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: Product_Repository) : ViewModel() {

    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products.asStateFlow()

    private val _addProductStatus = MutableSharedFlow<ApiResponse<String>>()
    val addProductStatus: SharedFlow<ApiResponse<String>> = _addProductStatus.asSharedFlow()

    private val _syncStatus = MutableSharedFlow<String>()
    val syncStatus: SharedFlow<String> = _syncStatus.asSharedFlow()


    fun fetchProducts() {
        viewModelScope.launch {
            repository.getProducts().collect { productList ->
                _products.value = productList
            }
        }
    }

    fun addProduct(addProductDetail: Add_Product_detail, isNetworkAvailable: Boolean) {
        viewModelScope.launch {
            repository.addProduct(addProductDetail, isNetworkAvailable).collect { response ->
                _addProductStatus.emit(response)
            }
        }
    }


    fun syncProducts() {
        viewModelScope.launch {
           repository.syncProductsFromApi()
            _syncStatus.emit("Products Synced Successfully")
        }
    }


    fun uploadPendingProducts() {
        viewModelScope.launch {
            repository.uploadPendingProducts()
            _syncStatus.emit("Pending Products Uploaded Successfully")
        }
    }
}
