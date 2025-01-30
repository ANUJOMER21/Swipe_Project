package com.swipe.test_project

import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.swipe.test_project.Screens.HomeScreen
import com.swipe.test_project.Screens.SplashScreen
import com.swipe.test_project.Worker.NetworkChangeReceiver
import com.swipe.test_project.Worker.PendingProductUpload_worker
import com.swipe.test_project.ui.theme.Swipe_ProjectTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Swipe_ProjectTheme {
                Scaffold {
                    pading->
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "splash",
                        modifier = Modifier.padding(pading)) {
                        composable("splash") { SplashScreen(){
                            navController.navigate("home") } }
                        composable("home") { HomeScreen() }
                    }
                }

            }
        }
    }



}
