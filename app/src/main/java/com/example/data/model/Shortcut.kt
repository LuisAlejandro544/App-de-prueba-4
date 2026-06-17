package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "shortcuts")
@JsonClass(generateAdapter = true)
data class Shortcut(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String,
    val iconName: String,
    val actions: List<ShortcutAction> = emptyList(),
    val triggerTime: String? = null,
    val isTriggerEnabled: Boolean = false,
    val isAutoTrigger: Boolean = false,
    val systemTrigger: String? = null
)
