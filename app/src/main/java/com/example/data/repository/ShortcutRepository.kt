package com.example.data.repository

import com.example.data.database.ShortcutDao
import com.example.data.model.Shortcut
import kotlinx.coroutines.flow.Flow

class ShortcutRepository(private val shortcutDao: ShortcutDao) {
    val allShortcuts: Flow<List<Shortcut>> = shortcutDao.getAllShortcuts()

    suspend fun getShortcutById(id: Int): Shortcut? = shortcutDao.getShortcutById(id)

    suspend fun insert(shortcut: Shortcut): Long = shortcutDao.insertShortcut(shortcut)

    suspend fun update(shortcut: Shortcut) = shortcutDao.updateShortcut(shortcut)

    suspend fun delete(shortcut: Shortcut) = shortcutDao.deleteShortcut(shortcut)
}
