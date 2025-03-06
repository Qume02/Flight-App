package com.example.flightapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert
    suspend fun insertFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): Flow<List<Favorite>>

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun deleteFavoriteByCodes(departureCode: String, destinationCode: String)

    @Query("SELECT * FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun getFavoriteByCodes(departureCode: String, destinationCode: String): Favorite?
}