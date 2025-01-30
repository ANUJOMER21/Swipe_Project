package com.swipe.test_project.DI

import com.swipe.test_project.Api.Product_api_service
import com.swipe.test_project.Repository.Product_Repository
import com.swipe.test_project.Room.ProductDatabase
import com.swipe.test_project.ViewModel.ProductViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val appModule= module {
    single {
        Retrofit.Builder()
            .baseUrl("https://app.getswipe.in/api/public/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Product_api_service::class.java)
    }


    single {
        ProductDatabase.getDatabase(androidContext()).productDao()
    }

    single { Product_Repository(get(), get()) }


    viewModel { ProductViewModel(get()) }
}