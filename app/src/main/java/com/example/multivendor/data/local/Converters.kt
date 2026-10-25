package com.example.multivendor.data.local

import androidx.room.TypeConverter
import com.example.multivendor.data.OrderLine
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

/** Room type converters for JSON-encoded columns. */
class Converters {
    @TypeConverter
    fun fromTagsJson(value: String): List<String> = runCatching {
        json.decodeFromString(ListSerializer(String.serializer()), value)
    }.getOrDefault(emptyList())

    @TypeConverter
    fun toTagsJson(value: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), value)

    @TypeConverter
    fun fromOrderLinesJson(value: String): List<OrderLine> = runCatching {
        json.decodeFromString(ListSerializer(OrderLine.serializer()), value)
    }.getOrDefault(emptyList())

    @TypeConverter
    fun toOrderLinesJson(value: List<OrderLine>): String =
        json.encodeToString(ListSerializer(OrderLine.serializer()), value)
}
