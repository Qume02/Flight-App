package com.example.flightapp.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

//@Database(entities = [Airport::class, Favorite::class], version = 1, exportSchema = false)
//abstract class FlightDatabase : RoomDatabase() {
//    abstract fun airportDao(): AirportDao
//    abstract fun favoriteDao(): FavoriteDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: FlightDatabase? = null
//
//        fun getDatabase(context: Context): FlightDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(context, FlightDatabase::class.java,"flight_search")
//                    .createFromAsset("database/flight_search.db")
//                    .fallbackToDestructiveMigration()
//                    .build()
//                    .also { INSTANCE = it }
//                instance
//            }
//        }
//    }
//}







@Database(entities = [Airport::class, Favorite::class], version = 1, exportSchema = false)
abstract class FlightDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        private const val DATABASE_NAME = "flight_search.db" // Keep the same database name
        private const val ASSET_DATABASE_PATH = "database/$DATABASE_NAME" // Path in assets

        @Volatile
        private var INSTANCE: FlightDatabase? = null

        fun getDatabase(context: Context): FlightDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                instance
            }
        }

        private fun buildDatabase(context: Context): FlightDatabase {
            val databaseFile = context.getDatabasePath(DATABASE_NAME)

            if (!databaseFile.exists()) {
                Log.d("FlightDatabase", "Database file does not exist, copying from assets...")
                copyDatabaseFromAssets(context)
            } else {
                Log.d("FlightDatabase", "Database file already exists at ${databaseFile.path}")
            }

            return Room.databaseBuilder(context.applicationContext, FlightDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }

        private fun copyDatabaseFromAssets(context: Context) {
            val inputStream: InputStream = context.assets.open(ASSET_DATABASE_PATH)
            val outputStream: FileOutputStream

            try {
                val databaseFile = context.getDatabasePath(DATABASE_NAME)
                databaseFile.parentFile?.mkdirs() // Ensure directory exists
                outputStream = FileOutputStream(databaseFile)

                inputStream.copyTo(outputStream) // Kotlin extension function for efficient copy

                inputStream.close()
                outputStream.flush()
                outputStream.close()
                Log.d("FlightDatabase", "Database copied successfully to ${databaseFile.path}")

            } catch (e: IOException) {
                Log.e("FlightDatabase", "Error copying database from assets", e)
                throw RuntimeException("Error creating source database", e) // Or handle error more gracefully
            }
        }
    }
}
