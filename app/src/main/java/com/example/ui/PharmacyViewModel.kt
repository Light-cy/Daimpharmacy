package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PharmacyViewModel(
    application: Application,
    private val repository: PharmacyRepository
) : AndroidViewModel(application) {

    // --- Scroll Restoration States ---
    var savedMedicineGridIndex by mutableStateOf(0)
    var savedMedicineGridOffset by mutableStateOf(0)

    var savedDoctorOrdersIndex by mutableStateOf(0)
    var savedDoctorOrdersOffset by mutableStateOf(0)

    var savedAdminOrdersIndex by mutableStateOf(0)
    var savedAdminOrdersOffset by mutableStateOf(0)

    var savedAdminMedicinesIndex by mutableStateOf(0)
    var savedAdminMedicinesOffset by mutableStateOf(0)

    var savedAdminCategoriesIndex by mutableStateOf(0)
    var savedAdminCategoriesOffset by mutableStateOf(0)

    var savedAdminDoctorsIndex by mutableStateOf(0)
    var savedAdminDoctorsOffset by mutableStateOf(0)

    // --- Authentication ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _loginLoading = MutableStateFlow(false)
    val loginLoading: StateFlow<Boolean> = _loginLoading.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        restoreSession()
        triggerFirebaseSync()
        viewModelScope.launch {
            kotlinx.coroutines.withTimeoutOrNull(1500) {
                combine(repository.allMedicines, repository.allOrders) { meds, ords ->
                    meds.isNotEmpty() || ords.isNotEmpty()
                }.firstOrNull { it }
            }
            _isLoading.value = false
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val context = getApplication<Application>()
            FirebaseSyncHelper.pullMedicinesFromFirestore(
                context = context,
                repository = repository,
                scope = viewModelScope
            )
            // Wait briefly to show refresh indicator
            kotlinx.coroutines.delay(1000)
            _isRefreshing.value = false
        }
    }

    fun triggerFirebaseSync() {
        val context = getApplication<Application>()
        FirebaseSyncHelper.pullMedicinesFromFirestore(
            context = context,
            repository = repository,
            scope = viewModelScope
        )
        FirebaseSyncHelper.startRealtimeSync(
            context = context,
            repository = repository,
            scope = viewModelScope
        )
        FirebaseSyncHelper.startRealtimeOrdersSync(
            context = context,
            repository = repository,
            scope = viewModelScope
        )
        FirebaseSyncHelper.startRealtimeUsersSync(
            context = context,
            repository = repository,
            scope = viewModelScope
        )
        FirebaseSyncHelper.startRealtimeCategoriesSync(
            context = context,
            repository = repository,
            scope = viewModelScope
        )
        FirebaseSyncHelper.syncAllLocalUsersToFirestore(
            context = context,
            repository = repository,
            scope = viewModelScope
        )
    }

    private fun restoreSession() {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("user_session_pref", android.content.Context.MODE_PRIVATE)
        val savedEmail = sharedPrefs.getString("saved_user_email", null)
        if (!savedEmail.isNullOrEmpty()) {
            viewModelScope.launch {
                val user = repository.getUserByEmailDirect(savedEmail)
                if (user != null && user.isActive) {
                    _currentUser.value = user
                }
            }
        }
    }

    // --- Search and Filters ---
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- Cart ---
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val cartTotal: Flow<Double> = _cartItems.map { items ->
        items.sumOf { it.price * it.quantity }
    }

    // --- Master Data Flows ---
    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isCategoriesLoading: StateFlow<Boolean> = repository.isCategoriesLoading
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val allMedicines: StateFlow<List<MedicineEntity>> = repository.allMedicines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Doctor Orders ---
    val doctorOrders: StateFlow<List<OrderEntity>> = _currentUser
        .filterNotNull()
        .flatMapLatest { user ->
            repository.getOrdersByDoctor(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Filtered Medicines ---
    val filteredMedicines: StateFlow<List<MedicineEntity>> = combine(
        repository.allMedicines,
        _selectedCategory,
        _searchQuery
    ) { medicines, category, query ->
        medicines.filter { med ->
            val matchesCategory = category == null || med.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isEmpty() ||
                    med.name.contains(query, ignoreCase = true) ||
                    med.formula.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Actions ---

    fun login(email: String, role: String, password: String = "", onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _loginLoading.value = true
            _loginError.value = null
            
            if (email.trim().isBlank()) {
                _loginError.value = "Email address cannot be empty."
                _loginLoading.value = false
                return@launch
            }
            if (password.trim().isBlank()) {
                _loginError.value = "Password cannot be empty."
                _loginLoading.value = false
                return@launch
            }
            
            val user = repository.getUserByEmailDirect(email.trim().lowercase())
            if (user != null) {
                if (!user.isActive) {
                    _loginError.value = "This account has been deactivated."
                } else if (!user.role.equals(role, ignoreCase = true)) {
                    _loginError.value = "Invalid role for this account."
                } else if (user.password != password.trim()) {
                    _loginError.value = "Incorrect password."
                } else {
                    _currentUser.value = user
                    val sharedPrefs = getApplication<Application>().getSharedPreferences("user_session_pref", android.content.Context.MODE_PRIVATE)
                    sharedPrefs.edit().putString("saved_user_email", user.email).apply()
                    onSuccess?.invoke()
                }
            } else {
                _loginError.value = "Account not found. Ask admin to create your account."
            }
            _loginLoading.value = false
        }
    }

    fun logout() {
        _currentUser.value = null
        _cartItems.value = emptyList()
        _selectedCategory.value = null
        _searchQuery.value = ""
        val sharedPrefs = getApplication<Application>().getSharedPreferences("user_session_pref", android.content.Context.MODE_PRIVATE)
        sharedPrefs.edit().remove("saved_user_email").apply()
    }

    fun selectCategory(categoryName: String?) {
        _selectedCategory.value = if (_selectedCategory.value == categoryName) null else categoryName
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- Cart Actions ---
    fun addToCart(medicine: MedicineEntity, quantity: Int) {
        if (quantity <= 0) return
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.medicineId == medicine.id }

        if (existingIndex != -1) {
            val existingItem = currentList[existingIndex]
            currentList[existingIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            currentList.add(
                CartItem(
                    medicineId = medicine.id,
                    medicineName = medicine.name,
                    formula = medicine.formula,
                    category = medicine.category,
                    quantity = quantity,
                    price = medicine.price
                )
            )
        }
        _cartItems.value = currentList
    }

    fun updateCartQuantity(medicineId: Int, newQuantity: Int) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.medicineId == medicineId }
        if (index != -1) {
            if (newQuantity <= 0) {
                currentList.removeAt(index)
            } else {
                currentList[index] = currentList[index].copy(quantity = newQuantity)
            }
            _cartItems.value = currentList
        }
    }

    fun removeFromCart(medicineId: Int) {
        _cartItems.value = _cartItems.value.filter { it.medicineId != medicineId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun placeOrder(onSuccess: () -> Unit, onError: ((String) -> Unit)? = null) {
        val doctor = _currentUser.value
        if (doctor == null) {
            onError?.invoke("You must be logged in to place an order.")
            return
        }
        val items = _cartItems.value
        if (items.isEmpty()) {
            onError?.invoke("Your cart is empty! Add some medicines before placing an order.")
            return
        }

        viewModelScope.launch {
            val jsonItems = cartItemsToJson(items)
            val order = OrderEntity(
                doctorId = doctor.id,
                doctorName = doctor.name,
                status = "pending",
                itemsJson = jsonItems
            )
            val orderId = repository.insertOrder(order).toInt()
            val syncedOrder = order.copy(id = orderId)
            
            // Sync to Firestore
            FirebaseSyncHelper.syncOrderToFirestore(getApplication(), syncedOrder)
            
            // Save as doctor's last order
            val updatedDoctor = doctor.copy(lastOrderItemsJson = jsonItems)
            repository.updateUser(updatedDoctor)
            _currentUser.value = updatedDoctor

            // Clear cart & callback
            _cartItems.value = emptyList()
            onSuccess()

            // Trigger local confirmation notification to the doctor
            NotificationHelper.showNotification(
                getApplication(),
                "Order Placed Successfully",
                "Your order has been submitted to the pharmacy.",
                orderId
            )
        }
    }

    fun repeatLastOrder() {
        val doctor = _currentUser.value ?: return
        if (doctor.lastOrderItemsJson.isNotEmpty()) {
            val items = jsonToCartItems(doctor.lastOrderItemsJson)
            _cartItems.value = items
        }
    }

    // --- Admin Actions ---
    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            
            // Sync status to Firestore
            FirebaseSyncHelper.updateOrderStatusInFirestore(getApplication(), orderId, status)
        }
    }

    fun addMedicine(name: String, formula: String, category: String, price: Double, stock: Int, imageUri: String? = null) {
        viewModelScope.launch {
            var finalizedImageUri = imageUri
            val isFirebase = FirebaseSyncHelper.isFirebaseEnabled(getApplication())
            
            if (isFirebase && imageUri != null) {
                val uploadedUrl = FirebaseSyncHelper.uploadMedicineImage(getApplication(), imageUri)
                if (uploadedUrl != null) {
                    finalizedImageUri = uploadedUrl
                }
            }

            val entity = MedicineEntity(
                name = name,
                formula = formula,
                category = category,
                price = price,
                stock = stock,
                imageUri = finalizedImageUri
            )
            
            val newRowId = repository.insertMedicine(entity)
            val insertedMedicine = entity.copy(id = newRowId.toInt())

            if (isFirebase) {
                FirebaseSyncHelper.syncMedicineToFirestore(getApplication(), insertedMedicine)
            }
        }
    }

    fun updateMedicine(medicine: MedicineEntity) {
        viewModelScope.launch {
            var finalizedImageUri = medicine.imageUri
            val isFirebase = FirebaseSyncHelper.isFirebaseEnabled(getApplication())
            
            if (isFirebase && medicine.imageUri != null) {
                val uploadedUrl = FirebaseSyncHelper.uploadMedicineImage(getApplication(), medicine.imageUri)
                if (uploadedUrl != null) {
                    finalizedImageUri = uploadedUrl
                }
            }

            val updatedMedicine = medicine.copy(imageUri = finalizedImageUri)
            repository.updateMedicine(updatedMedicine)

            if (isFirebase) {
                FirebaseSyncHelper.syncMedicineToFirestore(getApplication(), updatedMedicine)
            }
        }
    }

    fun deleteMedicine(medicine: MedicineEntity) {
        viewModelScope.launch {
            repository.deleteMedicine(medicine)
            if (FirebaseSyncHelper.isFirebaseEnabled(getApplication())) {
                FirebaseSyncHelper.deleteMedicineFromFirestore(getApplication(), medicine)
            }
        }
    }

    fun addCategory(name: String, iconName: String) {
        viewModelScope.launch {
            val id = java.util.UUID.randomUUID().toString()
            repository.insertCategory(CategoryEntity(id = id, name = name, iconName = iconName))
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun createAccount(name: String, email: String, role: String, password: String) {
        viewModelScope.launch {
            val newUser = UserEntity(
                id = email.trim().lowercase(),
                name = name,
                email = email.trim().lowercase(),
                role = role.trim().lowercase(),
                isActive = true,
                password = password
            )
            repository.insertUser(newUser)
            FirebaseSyncHelper.syncUserToFirestore(getApplication(), newUser)
        }
    }

    fun updateAccount(user: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(user)
            FirebaseSyncHelper.syncUserToFirestore(getApplication(), user)
        }
    }

    fun createDoctorAccount(name: String, email: String) {
        createAccount(name, email, "doctor", "123456")
    }

    fun toggleDoctorActiveStatus(user: UserEntity) {
        viewModelScope.launch {
            val updatedUser = user.copy(isActive = !user.isActive)
            repository.updateUser(updatedUser)
            FirebaseSyncHelper.syncUserToFirestore(getApplication(), updatedUser)
        }
    }

    // --- Inline Serialisation Helpers for safety ---
    private fun cartItemsToJson(items: List<CartItem>): String {
        val builder = StringBuilder()
        builder.append("[")
        for (i in items.indices) {
            val item = items[i]
            builder.append("{")
            builder.append("\"medicineId\":${item.medicineId},")
            builder.append("\"medicineName\":\"${item.medicineName.replace("\"", "\\\"")}\",")
            builder.append("\"formula\":\"${item.formula.replace("\"", "\\\"")}\",")
            builder.append("\"category\":\"${item.category.replace("\"", "\\\"")}\",")
            builder.append("\"quantity\":${item.quantity},")
            builder.append("\"price\":${item.price}")
            builder.append("}")
            if (i < items.size - 1) {
                builder.append(",")
            }
        }
        builder.append("]")
        return builder.toString()
    }

    fun jsonToCartItems(json: String): List<CartItem> {
        val items = mutableListOf<CartItem>()
        if (json.isEmpty() || json == "[]") return items
        try {
            val clean = json.trim().removePrefix("[").removeSuffix("]")
            if (clean.isEmpty()) return items
            
            val parts = clean.split("},")
            for (part in parts) {
                var itemStr = part.trim()
                if (!itemStr.endsWith("}")) {
                    itemStr += "}"
                }
                itemStr = itemStr.removePrefix("{").removeSuffix("}")
                
                var medId = 0
                var medName = ""
                var formula = ""
                var category = ""
                var quantity = 0
                var price = 0.0
                
                val fields = itemStr.split(",")
                for (field in fields) {
                    val pair = field.split(":")
                    if (pair.size >= 2) {
                        val key = pair[0].trim().replace("\"", "")
                        val value = pair.drop(1).joinToString(":").trim()
                        
                        when (key) {
                            "medicineId" -> medId = value.toIntOrNull() ?: 0
                            "medicineName" -> medName = value.removeSurrounding("\"")
                            "formula" -> formula = value.removeSurrounding("\"")
                            "category" -> category = value.removeSurrounding("\"")
                            "quantity" -> quantity = value.toIntOrNull() ?: 0
                            "price" -> price = value.toDoubleOrNull() ?: 0.0
                        }
                    }
                }
                items.add(CartItem(medId, medName, formula, category, quantity, price))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return items
    }
}

class PharmacyViewModelFactory(
    private val application: Application,
    private val repository: PharmacyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PharmacyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PharmacyViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
