package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String, // email or UUID
    val name: String,
    val email: String,
    val role: String, // "doctor" | "admin"
    val isActive: Boolean = true,
    val lastOrderItemsJson: String = "", // Cached JSON of last order items for "Repeat Last Order"
    val password: String = "123456"
)

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val formula: String,
    val category: String, // name of category
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val price: Double = 0.0,
    val stock: Int = 100,
    val imageUri: String? = null
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val iconName: String // name of material icon (e.g. "Tablets", "Syrups")
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val doctorId: String,
    val doctorName: String,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String, // "pending" | "ready" | "completed"
    val itemsJson: String // JSON representation of ordered items
)

// Helper structure representing items inside an order
data class CartItem(
    val medicineId: Int,
    val medicineName: String,
    val formula: String,
    val category: String,
    val quantity: Int,
    val price: Double
)
