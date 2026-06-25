package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow

class PharmacyRepository(private val db: AppDatabase) {
    private val userDao = db.userDao()
    private val medicineDao = db.medicineDao()
    private val orderDao = db.orderDao()

    // --- Users ---
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    
    fun getUserById(id: String): Flow<UserEntity?> = userDao.getUserById(id)
    
    suspend fun getUserByEmailDirect(email: String): UserEntity? = userDao.getUserByEmailDirect(email)
    
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    
    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
    
    suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)

    // --- Medicines ---
    val allMedicines: Flow<List<MedicineEntity>> = medicineDao.getAllMedicines()
    
    fun getMedicinesByCategory(category: String): Flow<List<MedicineEntity>> = 
        medicineDao.getMedicinesByCategory(category)
        
    suspend fun insertMedicine(medicine: MedicineEntity): Long = medicineDao.insertMedicine(medicine)
    
    suspend fun updateMedicine(medicine: MedicineEntity) = medicineDao.updateMedicine(medicine)
    
    suspend fun deleteMedicine(medicine: MedicineEntity) = medicineDao.deleteMedicine(medicine)

    // --- Categories ---
    private val _allCategories = kotlinx.coroutines.flow.MutableStateFlow<List<CategoryEntity>>(emptyList())
    val allCategories: Flow<List<CategoryEntity>> = _allCategories.asStateFlow()

    private val _isCategoriesLoading = kotlinx.coroutines.flow.MutableStateFlow(true)
    val isCategoriesLoading: Flow<Boolean> = _isCategoriesLoading.asStateFlow()

    fun updateCategories(list: List<CategoryEntity>) {
        _allCategories.value = list.sortedBy { it.name }
    }

    fun setCategoriesLoading(loading: Boolean) {
        _isCategoriesLoading.value = loading
    }

    suspend fun insertCategory(category: CategoryEntity) {
        try {
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val data = mapOf(
                "id" to category.id,
                "name" to category.name,
                "iconName" to category.iconName
            )
            firestore.collection("categories").document(category.id).set(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateCategory(category: CategoryEntity) {
        insertCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        try {
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            firestore.collection("categories").document(category.id).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- Orders ---
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    
    fun getOrdersByDoctor(doctorId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByDoctor(doctorId)
    
    fun getOrderById(orderId: Int): Flow<OrderEntity?> = orderDao.getOrderById(orderId)
    
    suspend fun getOrderByIdOnce(orderId: Int): OrderEntity? = orderDao.getOrderByIdOnce(orderId)
    
    suspend fun insertOrder(order: OrderEntity): Long = orderDao.insertOrder(order)
    
    suspend fun updateOrderStatus(orderId: Int, status: String) = orderDao.updateOrderStatus(orderId, status)
}
