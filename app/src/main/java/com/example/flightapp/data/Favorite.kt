package com.example.flightapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite",
    indices = [Index(value = ["departure_code", "destination_code"], unique = true)]
)
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "departure_code") val departureCode: String,
    @ColumnInfo(name = "destination_code") val destinationCode: String
)