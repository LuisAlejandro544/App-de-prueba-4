package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.model.ShortcutAction
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val actionListType = Types.newParameterizedType(List::class.java, ShortcutAction::class.java)
    private val adapter = moshi.adapter<List<ShortcutAction>>(actionListType)

    @TypeConverter
    fun fromString(value: String): List<ShortcutAction>? {
        if (value.isBlank()) return emptyList()
        return try {
            adapter.fromJson(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromActionList(list: List<ShortcutAction>?): String {
        if (list == null) return "[]"
        return try {
            adapter.toJson(list)
        } catch (e: Exception) {
            "[]"
        }
    }
}
