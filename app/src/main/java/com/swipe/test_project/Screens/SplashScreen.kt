package com.swipe.test_project.Screens

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import android.window.SplashScreen
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.swipe.test_project.R
import com.swipe.test_project.ViewModel.ProductViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.jar.Manifest
// Function to check if permission is already granted
fun checkPermissions(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}

// Function to request permission
fun requestPermissions(permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}
@Composable
fun SplashScreen(viewModel: ProductViewModel = koinViewModel(), navigateToHome: () -> Unit) {
    val context = LocalContext.current

    // Permission request launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Permission Granted!", Toast.LENGTH_SHORT).show()
            viewModel.syncProducts() // Start sync after permission is granted
        } else {
            Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    val syncStatus by rememberUpdatedState(viewModel.syncStatus)

    // Check and request permission when the screen loads
    LaunchedEffect(Unit) {
        if (!checkPermissions(context)) {
            requestPermissions(permissionLauncher)
        } else {
            viewModel.syncProducts() // Start syncing if permission is already granted
        }
    }

    // Listen for sync completion
    LaunchedEffect(syncStatus) {
        syncStatus.collect {
            navigateToHome()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Image(
                painter = rememberAsyncImagePainter(R.drawable.sample),
                contentDescription = "Sample Image",
                modifier = Modifier
                    .padding(16.dp)
                    .size(200.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

