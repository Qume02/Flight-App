package com.example.flightapp.data

import android.util.Log
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport WHERE name LIKE '%' || :query || '%' OR iata_code LIKE '%' || :query || '%' ORDER BY passengers DESC")
    fun getAirportsByNameOrIata(query: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport")
    fun getDestinationsByDeparture(): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    fun getAirportByIataCode(iataCode: String): Airport?
}