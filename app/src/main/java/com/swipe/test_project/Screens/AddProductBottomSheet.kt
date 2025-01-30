package com.swipe.test_project.Screens
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.rememberAsyncImagePainter
import com.swipe.test_project.Api.Add_Product_detail
import com.swipe.test_project.Api.ApiResponse
import com.swipe.test_project.ViewModel.ProductViewModel
import com.swipe.test_project.utils.Network_utils
import org.koin.androidx.compose.koinViewModel

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
@Composable
fun AddProductBottomSheet(onDismiss: () -> Unit, viewModel: ProductViewModel = koinViewModel(), context: Context) {
    var productName by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var tax by remember { mutableStateOf("") }
    var productImageUri by remember { mutableStateOf<Uri?>(null) } // Store URI
    var isLoading by remember { mutableStateOf(false) } // Track loading state
    var addProductResponse by remember { mutableStateOf<String?>(null) } // Track the response
    var errorMessage by remember { mutableStateOf<String?>(null) } // Error message for validation
    var showSuccessDialog by remember { mutableStateOf(false) } // Show success dialog

    // Register the activity result contract for image selection
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                productImageUri = uri // Save the URI
            }
        }
    )

    // Collect the addProductStatus SharedFlow
    LaunchedEffect(key1 = viewModel) {
        viewModel.addProductStatus.collect { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    isLoading = true // Show progress
                }
                is ApiResponse.Success -> {
                    val message=response.data
                    isLoading = false
                    addProductResponse = "$message"
                    showSuccessDialog = true

                }
                is ApiResponse.Error -> {
                    isLoading = false
                    addProductResponse = "Failed to add product: ${response.message}"
                    // Show a notification or dialog to the user for failure
                }
            }
        }
    }
    fun getFileName(uri: Uri): String {
        var name = ""
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val columnIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                if (it.moveToFirst() && columnIndex != -1) {
                    name = it.getString(columnIndex)
                }
            }
        }
        return name.ifEmpty { "image_${System.currentTimeMillis()}.jpg" }
    }
    // Function to convert URI to File
    fun uriToFile(uri: Uri): File? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val fileName = getFileName(uri)
            val tempFile = File(context.cacheDir, fileName)

            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.let { input ->
                val outputStream = FileOutputStream(tempFile)
                input.copyTo(outputStream)
                input.close()
                outputStream.close()
            }

            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Allow scrolling if keyboard appears
    ) {
        // Title
        Text(
            text = "Add Product",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp) // Space after title
        )

        // Product Name, Type, Price, Tax - TextFields...
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = productType,
            onValueChange = { productType = it },
            label = { Text("Product Type") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = tax,
            onValueChange = { tax = it },
            label = { Text("Tax") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Product Image Section
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Product Image", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*") // Launch image picker
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (productImageUri != null) "Change Image" else "Select Image")
            }

            // Show the selected image preview
            productImageUri?.let {
                Spacer(modifier = Modifier.height(8.dp))
                val painter = rememberAsyncImagePainter(it)
                Image(
                    painter = painter,
                    contentDescription = "Selected Product Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // You can adjust the height as needed
                        .padding(8.dp) // Optional padding around the image
                )
            }
        }

        // Show loading progress dialog
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }


        // Show error message if validation fails
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
        val isNetworkavailble=Network_utils.isNetworkAvailableSimplified(LocalContext.current)
        // Add Product Button
        Button(
            onClick = {
                // Validate fields
                if (productName.isBlank() || productType.isBlank() || price.isBlank() || tax.isBlank() || productImageUri == null) {
                    errorMessage = "Please fill in all fields and select an image."
                } else {

                    val productImageFile = productImageUri?.let { uriToFile(it) }
                    Log.d("product_image", productImageFile.toString())
                    val product = Add_Product_detail(productName, productType, price, tax, image = productImageFile!!)
                    viewModel.addProduct(product, isNetworkAvailable = isNetworkavailble)
                    productName = ""
                    productType = ""
                    price = ""
                    tax = ""
                    errorMessage = null

                    productImageUri = null // Clear the URI

                    //onDismiss() // Dismiss the bottom sheet after adding the product
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Add Product")
        }
    }
    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Success") },
            text = { Text("$addProductResponse") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false // Close the dialog
                        onDismiss() // Close the bottom sheet
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
