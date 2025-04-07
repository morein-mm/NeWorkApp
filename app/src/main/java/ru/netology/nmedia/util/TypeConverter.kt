package ru.netology.nmedia.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.UserPreview
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val listType = TypeToken.getParameterized(List::class.java, String::class.java).type

class TypeConverter {

    @TypeConverter
    fun fromListIntToString(list: List<Int>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toListIntFromString(value: String): List<Int> {
        val type = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, type)
    }
}


object UserPreviewConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromMap(value: Map<Long, UserPreview>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMap(value: String): Map<Long, UserPreview>? {
        val type = object : TypeToken<Map<Long, UserPreview>>() {}.type
        return gson.fromJson(value, type)
    }
}

object ListLongConverter {

    @TypeConverter
    fun fromListLong(list: List<Long>?): String? {
        return if (list == null) null else Gson().toJson(list)
    }

    @TypeConverter
    fun toListLong(json: String?): List<Long>? {
        return if (json == null) null else Gson().fromJson(
            json,
            object : TypeToken<List<Long>>() {}.type
        )
    }
}