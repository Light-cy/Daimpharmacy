package com.example.data

import android.content.Context
import android.net.Uri
import com.example.ui.NotificationHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume

object FirebaseSyncHelper {

    fun isFirebaseEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences("firebase_settings", Context.MODE_PRIVATE)
        return prefs.getBoolean("enabled", true)
    }

    fun setFirebaseEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences("firebase_settings", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("enabled", enabled).apply()
    }

    fun getFirebaseConfig(context: Context): FirebaseConfig {
        val prefs = context.getSharedPreferences("firebase_settings", Context.MODE_PRIVATE)
        return FirebaseConfig(
            projectId = prefs.getString("project_id", "daimpharmacy-8f95a") ?: "daimpharmacy-8f95a",
            apiKey = prefs.getString("api_key", "AIzaSyCgnLBkzlBrDmU9Y9ARGBTE0mH_UmTW4qM") ?: "AIzaSyCgnLBkzlBrDmU9Y9ARGBTE0mH_UmTW4qM",
            storageBucket = prefs.getString("storage_bucket", "daimpharmacy-8f95a.firebasestorage.app") ?: "daimpharmacy-8f95a.firebasestorage.app",
            applicationId = prefs.getString("app_id", "1:697636229555:android:94394790c868185338fbc6") ?: "1:697636229555:android:94394790c868185338fbc6"
        )
    }

    fun saveFirebaseConfig(context: Context, config: FirebaseConfig) {
        val prefs = context.getSharedPreferences("firebase_settings", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("project_id", config.projectId.trim())
            .putString("api_key", config.apiKey.trim())
            .putString("storage_bucket", config.storageBucket.trim())
            .putString("app_id", config.applicationId.trim())
            .apply()
    }

    fun initializeFirebase(context: Context): Boolean {
        if (!isFirebaseEnabled(context)) return false
        val config = getFirebaseConfig(context)
        if (config.projectId.isBlank() || config.apiKey.isBlank() || config.applicationId.isBlank()) {
            return false
        }

        try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isNotEmpty()) {
                return true
            }
            val options = FirebaseOptions.Builder()
                .setProjectId(config.projectId)
                .setApiKey(config.apiKey)
                .setStorageBucket(if (config.storageBucket.isBlank()) "${config.projectId}.appspot.com" else config.storageBucket)
                .setApplicationId(config.applicationId)
                .build()
            FirebaseApp.initializeApp(context, options)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun uploadMedicineImage(context: Context, localUriStr: String): String? {
        if (!initializeFirebase(context)) return null
        
        // If it is already a web URL, don't upload it again
        if (localUriStr.startsWith("http://") || localUriStr.startsWith("https://")) {
            return localUriStr
        }

        val fileUri = Uri.parse(localUriStr)
        return suspendCancellableCoroutine { continuation ->
            try {
                val storage = FirebaseStorage.getInstance()
                val filename = "medicines/${UUID.randomUUID()}.jpg"
                val ref = storage.reference.child(filename)
                
                ref.putFile(fileUri)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                            continuation.resume(downloadUrl.toString())
                        }.addOnFailureListener {
                            continuation.resume(null)
                        }
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                continuation.resume(null)
            }
        }
    }

    suspend fun syncMedicineToFirestore(context: Context, medicine: MedicineEntity) {
        if (!initializeFirebase(context)) return
        try {
            val db = FirebaseFirestore.getInstance()
            val data = mapOf(
                "id" to medicine.id,
                "name" to medicine.name,
                "formula" to medicine.formula,
                "category" to medicine.category,
                "price" to medicine.price,
                "stock" to medicine.stock,
                "imageUri" to medicine.imageUri
            )
            db.collection("medicines").document(medicine.id.toString()).set(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteMedicineFromFirestore(context: Context, medicine: MedicineEntity) {
        if (!initializeFirebase(context)) return
        try {
            val db = FirebaseFirestore.getInstance()
            db.collection("medicines").document(medicine.id.toString()).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pullMedicinesFromFirestore(context: Context, repository: PharmacyRepository, scope: CoroutineScope, onSyncComplete: (() -> Unit)? = null) {
        if (!initializeFirebase(context)) return
        try {
            val db = FirebaseFirestore.getInstance()
            db.collection("medicines").get()
                .addOnSuccessListener { querySnapshot ->
                    scope.launch(Dispatchers.IO) {
                        var hasNewItems = false
                        for (doc in querySnapshot.documents) {
                            val id = doc.getLong("id")?.toInt() ?: continue
                            val name = doc.getString("name") ?: ""
                            val formula = doc.getString("formula") ?: ""
                            val category = doc.getString("category") ?: ""
                            val price = doc.getDouble("price") ?: 0.0
                            val stock = doc.getLong("stock")?.toInt() ?: 0
                            val imageUri = doc.getString("imageUri")

                            val entity = MedicineEntity(
                                id = id,
                                name = name,
                                formula = formula,
                                category = category,
                                price = price,
                                stock = stock,
                                imageUri = imageUri
                            )
                            repository.insertMedicine(entity)
                            hasNewItems = true
                        }
                        if (hasNewItems) {
                            onSyncComplete?.invoke()
                        }
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startRealtimeSync(context: Context, repository: PharmacyRepository, scope: CoroutineScope) {
        if (!initializeFirebase(context)) return
        try {
            val db = FirebaseFirestore.getInstance()
            db.collection("medicines").addSnapshotListener { snapshots, e ->
                if (e != null) {
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    scope.launch(Dispatchers.IO) {
                        for (doc in snapshots.documents) {
                            val id = doc.getLong("id")?.toInt() ?: continue
                            val name = doc.getString("name") ?: ""
                            val formula = doc.getString("formula") ?: ""
                            val category = doc.getString("category") ?: ""
                            val price = doc.getDouble("price") ?: 0.0
                            val stock = doc.getLong("stock")?.toInt() ?: 0
                            val imageUri = doc.getString("imageUri")

                            val entity = MedicineEntity(
                                id = id,
                                name = name,
                                formula = formula,
                                category = category,
                                price = price,
                                stock = stock,
                                imageUri = imageUri
                            )
                            repository.insertMedicine(entity)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncOrderToFirestore(context: Context, order: OrderEntity) {
        if (!initializeFirebase(context)) return
        try {
            val db = FirebaseFirestore.getInstance()
            val data = mapOf(
                "id" to order.id,
                "doctorId" to order.doctorId,
                "doctorName" to order.doctorName,
                "createdAt" to order.createdAt,
                "status" to order.status,
                "itemsJson" to order.itemsJson
            )
            db.collection("orders").document(order.id.toString()).set(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateOrderStatusInFirestore(context: Context, orderId: Int, status: String) {
        if (!initializeFirebase(context)) return
        try {
            val db = FirebaseFirestore.getInstance()
            db.collection("orders").document(orderId.toString()).update("status", status)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startRealtimeOrdersSync(context: Context, repository: PharmacyRepository, scope: CoroutineScope) {
        if (!initializeFirebase(context)) return
        try {
            val db = FirebaseFirestore.getInstance()
            db.collection("orders").addSnapshotListener { snapshots, e ->
                if (e != null) {
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    scope.launch(Dispatchers.IO) {
                        // Retrieve current user details to target notifications appropriately
                        val sharedPrefs = context.getSharedPreferences("user_session_pref", Context.MODE_PRIVATE)
                        val savedEmail = sharedPrefs.getString("saved_user_email", null)
                        val currentUser = if (!savedEmail.isNullOrEmpty()) {
                            repository.getUserByEmailDirect(savedEmail)
                        } else {
                            null
                        }

                        for (change in snapshots.documentChanges) {
                            val doc = change.document
                            val id = doc.getLong("id")?.toInt() ?: continue
                            val doctorId = doc.getString("doctorId") ?: ""
                            val doctorName = doc.getString("doctorName") ?: ""
                            val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            val status = doc.getString("status") ?: "pending"
                            val itemsJson = doc.getString("itemsJson") ?: ""

                            val orderEntity = OrderEntity(
                                id = id,
                                doctorId = doctorId,
                                doctorName = doctorName,
                                createdAt = createdAt,
                                status = status,
                                itemsJson = itemsJson
                            )

                            val existing = repository.getOrderByIdOnce(id)
                            repository.insertOrder(orderEntity)

                            if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                                // Only show "New Order Received" notification to the Admin
                                if (existing == null && currentUser?.role == "admin") {
                                    NotificationHelper.showNotification(
                                        context,
                                        "New Order Received",
                                        "Order #$id from Dr. $doctorName is $status",
                                        id
                                    )
                                }
                            } else if (change.type == com.google.firebase.firestore.DocumentChange.Type.MODIFIED) {
                                // Only show order status updates to the specific doctor who placed the order
                                if (existing != null && existing.status != status) {
                                    if (currentUser?.role == "doctor" && currentUser.id == doctorId) {
                                        val notificationTitle = when (status.lowercase()) {
                                            "ready" -> "Order Ready for Pickup"
                                            "completed" -> "Order Completed"
                                            else -> "Order Status Updated"
                                        }
                                        val notificationMessage = when (status.lowercase()) {
                                            "ready" -> "Hi Dr. $doctorName, your order is ready for pickup."
                                            "completed" -> "Hi Dr. $doctorName, your order has been completed. Thank you!"
                                            else -> "Order #$id status changed to: $status"
                                        }
                                        NotificationHelper.showNotification(
                                            context,
                                            notificationTitle,
                                            notificationMessage,
                                            id
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncUserToFirestore(context: Context, user: UserEntity) {
        if (!initializeFirebase(context)) return
        try {
            val db = FirebaseFirestore.getInstance()
            val data = mapOf(
                "id" to user.id,
                "name" to user.name,
                "email" to user.email,
                "role" to user.role,
                "isActive" to user.isActive,
                "lastOrderItemsJson" to user.lastOrderItemsJson,
                "password" to user.password
            )
            db.collection("users").document(user.id).set(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun syncAllLocalUsersToFirestore(context: Context, repository: PharmacyRepository, scope: CoroutineScope) {
        if (!initializeFirebase(context)) return
        scope.launch(Dispatchers.IO) {
            try {
                val users = repository.allUsers.first()
                for (user in users) {
                    syncUserToFirestore(context, user)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startRealtimeUsersSync(context: Context, repository: PharmacyRepository, scope: CoroutineScope) {
        if (!initializeFirebase(context)) return
        try {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").addSnapshotListener { snapshots, e ->
                if (e != null) {
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    scope.launch(Dispatchers.IO) {
                        for (change in snapshots.documentChanges) {
                            val doc = change.document
                            val id = doc.getString("id") ?: continue
                            val name = doc.getString("name") ?: ""
                            val email = doc.getString("email") ?: ""
                            val role = doc.getString("role") ?: "doctor"
                            val isActive = doc.getBoolean("isActive") ?: true
                            val lastOrderItemsJson = doc.getString("lastOrderItemsJson") ?: ""
                            val password = doc.getString("password") ?: "123456"

                            val userEntity = UserEntity(
                                id = id,
                                name = name,
                                email = email,
                                role = role,
                                isActive = isActive,
                                lastOrderItemsJson = lastOrderItemsJson,
                                password = password
                            )
                            repository.insertUser(userEntity)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

data class FirebaseConfig(
    val projectId: String,
    val apiKey: String,
    val storageBucket: String,
    val applicationId: String
)
