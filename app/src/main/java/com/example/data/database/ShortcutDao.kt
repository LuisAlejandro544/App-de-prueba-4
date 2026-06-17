package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Shortcut
import kotlinx.coroutines.flow.Flow

@Dao
interface ShortcutDao {
    @Query("SELECT * FROM shortcuts ORDER BY id DESC")
    fun getAllShortcuts(): Flow<List<Shortcut>>

    @Query("SELECT * FROM shortcuts")
    suspend fun getAllShortcutsSync(): List<Shortcut>

    @Query("SELECT * FROM shortcuts WHERE id = :id")
    suspend fun getShortcutById(id: Int): Shortcut?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortcut(shortcut: Shortcut): Long

    @Update
    suspend fun updateShortcut(shortcut: Shortcut)

    @Delete
    suspend fun deleteShortcut(shortcut: Shortcut)
}
