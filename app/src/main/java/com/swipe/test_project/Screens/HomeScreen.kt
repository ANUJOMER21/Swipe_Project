package com.swipe.test_project.Screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swipe.test_project.R
import com.swipe.test_project.ViewModel.ProductViewModel
import com.swipe.test_project.utils.Network_utils
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: ProductViewModel = koinViewModel()) {
    val products by viewModel.products.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val sheetState = rememberStandardBottomSheetState(skipHiddenState = false)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)

    LaunchedEffect(Unit) {
        viewModel.fetchProducts()
    }




    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            AddProductBottomSheet(context = LocalContext.current,
                onDismiss = {
                coroutineScope.launch { sheetState.hide() }
            })
        },

        sheetPeekHeight = 0.dp,
        containerColor = Color.White
    ) { paddingValues ->


        Box(modifier = Modifier.fillMaxSize().padding(paddingValues))
        {
            if(products.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .align(Alignment.Center)
                ) {

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            // .clip(RoundedCornerShape(12.dp))
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(12.dp)
                            ),
                        //  .shadow(6.dp, RoundedCornerShape(12.dp)),
                        placeholder = {
                            Text(
                                "Search Product",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filteredProducts = products.filter {
                            it.product_name.contains(searchQuery, ignoreCase = true)
                        }
                        items(filteredProducts) { product ->
                            ProductItem(product)
                        }
                    }
                }
            }
            else {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.nodata), // Replace with your image
                            contentDescription = "No Image",
                            modifier = Modifier.size(200.dp)
                        )
                        Text(text = "No Data Found")
                    }
                }
            }
            FloatingActionButton(
                onClick = { coroutineScope.launch { sheetState.expand() } },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product", tint = Color.White)
            }
        }
    }
}




