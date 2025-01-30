package com.swipe.test_project.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {


    @Query("""
    SELECT * FROM Product_table 
    WHERE product_name = :name 
    AND product_type = :type 
    AND product_price = :price 
    AND tax = :tax
""")
    suspend fun findMatchingProduct(name: String, type: String, price: Double, tax: Double,): ProductModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
   fun insert_product(productModel: ProductModel)




   @Query("Select * from Product_table")
   fun get_all_product(): Flow<List<ProductModel>>

  @Query("Select * from Product_table where uploaded=0")
   fun get_non_uploaded_product(): Flow<List<ProductModel>>

    @Query("DELETE FROM Product_table where id=:id")
    suspend fun delete_products(id:String)
}