package com.example.data

import kotlinx.coroutines.flow.Flow

class PharmacyRepository(private val db: AppDatabase) {
    private val userDao = db.userDao()
    private val medicineDao = db.medicineDao()
    private val categoryDao = db.categoryDao()
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
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    
    suspend fun insertCategory(category: CategoryEntity) = categoryDao.insertCategory(category)
    
    suspend fun updateCategory(category: CategoryEntity) = categoryDao.updateCategory(category)
    
    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)

    // --- Orders ---
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    
    fun getOrdersByDoctor(doctorId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByDoctor(doctorId)
    
    fun getOrderById(orderId: Int): Flow<OrderEntity?> = orderDao.getOrderById(orderId)
    
    suspend fun getOrderByIdOnce(orderId: Int): OrderEntity? = orderDao.getOrderByIdOnce(orderId)
    
    suspend fun insertOrder(order: OrderEntity): Long = orderDao.insertOrder(order)
    
    suspend fun updateOrderStatus(orderId: Int, status: String) = orderDao.updateOrderStatus(orderId, status)
}
