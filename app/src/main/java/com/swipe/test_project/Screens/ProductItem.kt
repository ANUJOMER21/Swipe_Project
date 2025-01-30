package com.swipe.test_project.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import coil3.compose.rememberAsyncImagePainter
import com.swipe.test_project.Room.ProductModel
import com.swipe.test_project.Room.image_type
import com.swipe.test_project.R  // Import your drawable resources
import java.io.File
@Composable
fun ProductItem(product: ProductModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(16.dp))
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            //.shadow(8.dp, shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {


            val imagePainter = when {
                product.product_image.isEmpty() -> painterResource(R.drawable.img)
                product.image_type == image_type.net -> rememberAsyncImagePainter(product.product_image)
                else -> rememberAsyncImagePainter(File(product.product_image))
            }

            Image(
                painter = imagePainter,
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 12.dp)            )


            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = product.product_name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "₹${product.product_price} | Tax: ${product.tax}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = product.product_type,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        }
    }
}

