package com.swipe.test_project.Worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.swipe.test_project.Api.Product_api_service
import com.swipe.test_project.Repository.Product_Repository
import com.swipe.test_project.Room.ProductDatabase
import com.swipe.test_project.ViewModel.ProductViewModel
import com.swipe.test_project.utils.Network_utils
import org.koin.core.component.KoinComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PendingProductUpload_worker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams),KoinComponent
{

    override fun doWork(): Result {
       val api= Retrofit.Builder()
            .baseUrl("https://app.getswipe.in/api/public/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Product_api_service::class.java)
        val dao=   ProductDatabase.getDatabase(applicationContext).productDao()

        val repository= Product_Repository(api,dao)

        val viewModel= ProductViewModel(repository)


        val isInternetAvailable = Network_utils.isNetworkAvailableSimplified(applicationContext)
        if (isInternetAvailable) {
            Log.d("MyWorker", "Internet is available. Performing work.")

            viewModel.uploadPendingProducts()
            return Result.success()
        }
        else{
            Log.d("MyWorker", "No internet connection. Retrying...")


            return Result.retry()
        }
    }

}