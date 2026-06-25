package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class, MedicineEntity::class, OrderEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun medicineDao(): MedicineDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE medicines ADD COLUMN imageUri TEXT DEFAULT NULL")
            }
        }

        val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Just fallback / rebuilt, but we provide it for safety
            }
        }

        val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN password TEXT NOT NULL DEFAULT '123456'")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daim_pharmacy_db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(db: AppDatabase) {
            val userDao = db.userDao()
            val medicineDao = db.medicineDao()

            // 1. Seed Accounts
            // Admin account
            userDao.insertUser(
                UserEntity(
                    id = "admin@daim.com",
                    name = "Admin Worker",
                    email = "admin@daim.com",
                    role = "admin",
                    isActive = true,
                    password = "admin123"
                )
            )
            // Doctor 1
            userDao.insertUser(
                UserEntity(
                    id = "dr.ahmed@daim.com",
                    name = "Dr. Ahmed",
                    email = "dr.ahmed@daim.com",
                    role = "doctor",
                    isActive = true,
                    password = "doctor123"
                )
            )
            // Doctor 2
            userDao.insertUser(
                UserEntity(
                    id = "dr.fatima@daim.com",
                    name = "Dr. Fatima",
                    email = "dr.fatima@daim.com",
                    role = "doctor",
                    isActive = true,
                    password = "doctor123"
                )
            )

            // 3. Seed Medicines
            val medicines = listOf(
                MedicineEntity(
                    name = "Panadol",
                    formula = "Paracetamol 500mg",
                    category = "Tablets",
                    imageUrl = "",
                    isAvailable = true,
                    price = 15.0,
                    stock = 120,
                    imageUri = "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&q=80&w=400"
                ),
                MedicineEntity(
                    name = "Amoxil",
                    formula = "Amoxicillin 250mg/5ml",
                    category = "Syrups",
                    imageUrl = "",
                    isAvailable = true,
                    price = 85.0,
                    stock = 50,
                    imageUri = "https://images.unsplash.com/photo-1550572017-edd951b55104?auto=format&fit=crop&q=80&w=400"
                ),
                MedicineEntity(
                    name = "Ponstan Forte",
                    formula = "Mefenamic Acid 500mg",
                    category = "Tablets",
                    imageUrl = "",
                    isAvailable = true,
                    price = 30.0,
                    stock = 85,
                    imageUri = "https://images.unsplash.com/photo-1584017911766-d451b3b0e843?auto=format&fit=crop&q=80&w=400"
                ),
                MedicineEntity(
                    name = "Brufen",
                    formula = "Ibuprofen 400mg",
                    category = "Tablets",
                    imageUrl = "",
                    isAvailable = true,
                    price = 25.0,
                    stock = 100,
                    imageUri = "https://images.unsplash.com/photo-1603398938378-e54eab446dde?auto=format&fit=crop&q=80&w=400"
                ),
                MedicineEntity(
                    name = "Ventolin",
                    formula = "Salbutamol 2mg/5ml",
                    category = "Syrups",
                    imageUrl = "",
                    isAvailable = true,
                    price = 60.0,
                    stock = 40,
                    imageUri = "https://images.unsplash.com/photo-1527613426441-4da17471b66d?auto=format&fit=crop&q=80&w=400"
                ),
                MedicineEntity(
                    name = "Rocephin",
                    formula = "Ceftriaxone 1g",
                    category = "Injections",
                    imageUrl = "",
                    isAvailable = true,
                    price = 180.0,
                    stock = 30,
                    imageUri = "https://images.unsplash.com/photo-1579684389782-64d84b5e901a?auto=format&fit=crop&q=80&w=400"
                ),
                MedicineEntity(
                    name = "Loratadine",
                    formula = "Loratadine 10mg",
                    category = "Tablets",
                    imageUrl = "",
                    isAvailable = true,
                    price = 45.0,
                    stock = 60,
                    imageUri = "https://images.unsplash.com/photo-1550572017-edd951b55104?auto=format&fit=crop&q=80&w=400"
                ),
                MedicineEntity(
                    name = "Omeprazole",
                    formula = "Omeprazole 20mg",
                    category = "Capsules",
                    imageUrl = "",
                    isAvailable = true,
                    price = 55.0,
                    stock = 90,
                    imageUri = "https://images.unsplash.com/photo-1471864190281-a93a3070b6de?auto=format&fit=crop&q=80&w=400"
                ),
                MedicineEntity(
                    name = "Polyfax",
                    formula = "Polymyxin B Sulfate",
                    category = "Capsules",
                    imageUrl = "",
                    isAvailable = true,
                    price = 40.0,
                    stock = 25,
                    imageUri = "https://images.unsplash.com/photo-1584017911766-d451b3b0e843?auto=format&fit=crop&q=80&w=400"
                ),
                MedicineEntity(
                    name = "Diclofenac",
                    formula = "Diclofenac Sodium 75mg/3ml",
                    category = "Injections",
                    imageUrl = "",
                    isAvailable = true,
                    price = 70.0,
                    stock = 40,
                    imageUri = "https://images.unsplash.com/photo-1512290923902-8a9f81dc236c?auto=format&fit=crop&q=80&w=400"
                )
            )

            for (medicine in medicines) {
                medicineDao.insertMedicine(medicine)
            }
        }
    }
}
