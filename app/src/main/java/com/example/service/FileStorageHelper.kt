package com.example.service

import android.content.Context
import android.util.Log
import com.example.data.model.Shortcut
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper modular y robusto de almacenamiento bajo la filosofía "Desarrollo Ultra Modular".
 * Administra las carpetas organizadas dentro del espacio de la app:
 * - Android/data/com.example/files/shortcuts/ -> Para respaldos/archivos de atajos.
 * - Android/data/com.example/cache/custom_cache/ -> Caché rápida fácilmente borrable.
 * - Android/data/com.example/files/logs/warnings/ -> Logs de advertencia.
 * - Android/data/com.example/files/logs/crashes/ -> Logs de crasheadas.
 */
object FileStorageHelper {
    private const val TAG = "FileStorageHelper"

    // Punteros de archivos
    private var shortcutsDir: File? = null
    private var customCacheDir: File? = null
    private var logsWarningsDir: File? = null
    private var logsCrashesDir: File? = null

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val shortcutAdapter = moshi.adapter(Shortcut::class.java)

    /**
     * Inicializa la estructura organizada de carpetas.
     */
    fun initializeDirs(context: Context) {
        try {
            // Contexto seguro para obtener directorios de Android/data/com.example/
            val baseExternal = context.getExternalFilesDir(null) ?: context.filesDir
            val baseCache = context.externalCacheDir ?: context.cacheDir

            // 1. Carpeta para Guardar Atajos (Android/data/com.example/files/shortcuts)
            shortcutsDir = File(baseExternal, "shortcuts").apply {
                if (!exists()) mkdirs()
            }

            // 2. Carpeta para Caché (Android/data/com.example/cache/custom_cache)
            customCacheDir = File(baseCache, "custom_cache").apply {
                if (!exists()) mkdirs()
            }

            // 3. Carpeta de Logs con subcarpetas (Android/data/com.example/files/logs)
            val logsRoot = File(baseExternal, "logs")
            logsWarningsDir = File(logsRoot, "warnings").apply {
                if (!exists()) mkdirs()
            }
            logsCrashesDir = File(logsRoot, "crashes").apply {
                if (!exists()) mkdirs()
            }

            Log.d(TAG, "📁 Estructura de almacenamiento inicializada con éxito:")
            Log.d(TAG, "   -> Atajos: ${shortcutsDir?.absolutePath}")
            Log.d(TAG, "   -> Caché: ${customCacheDir?.absolutePath}")
            Log.d(TAG, "   -> Warnings: ${logsWarningsDir?.absolutePath}")
            Log.d(TAG, "   -> Crashes: ${logsCrashesDir?.absolutePath}")

            logWarning("System", "Almacenamiento de archivos iniciado de forma limpia.")
        } catch (e: Exception) {
            Log.e(TAG, "Error inicializando directorios de almacenamiento", e)
        }
    }

    /**
     * Configura el detector automático de fallos (Crash Handler) para registrar
     * crasheos inesperados directamente dentro de Android/data/com.example/files/logs/crashes/
     */
    fun setupCrashHandler() {
        val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // Registrar el crash en archivo físico
            logCrash(throwable)
            
            // Pasar la batuta al manejador original de Android para que termine correctamente
            originalHandler?.uncaughtException(thread, throwable)
        }
    }

    // --- MANEJO DE ARCHIVOS DE ATAJOS (RESPALDOS JSON) ---
    fun saveShortcutBackup(shortcut: Shortcut) {
        val dir = shortcutsDir ?: return
        try {
            val file = File(dir, "shortcut_backup_${shortcut.id}.json")
            val json = shortcutAdapter.toJson(shortcut)
            FileWriter(file).use { writer ->
                writer.write(json)
            }
            Log.d(TAG, "✓ Respaldo de atajo '${shortcut.name}' guardado en archivo.")
        } catch (e: Exception) {
            logWarning("Backup", "Fallo al guardar respaldo de atajo: ${shortcut.name}. Motivo: ${e.message}")
        }
    }

    fun getBackupFiles(): List<File> {
        val dir = shortcutsDir ?: return emptyList()
        return dir.listFiles { _, name -> name.endsWith(".json") }?.toList() ?: emptyList()
    }

    // --- MANEJO DE CACHÉ ---
    fun saveToCache(key: String, data: String) {
        val dir = customCacheDir ?: return
        try {
            val file = File(dir, "cache_$key.tmp")
            FileWriter(file).use { it.write(data) }
        } catch (e: Exception) {
            Log.e(TAG, "Error escribiendo en caché temporal personalizada", e)
        }
    }

    fun clearCustomCache() {
        try {
            val dir = customCacheDir ?: return
            dir.listFiles()?.forEach { it.delete() }
            Log.d(TAG, "✓ Caché personalizada limpiada satisfactoriamente.")
        } catch (e: Exception) {
            logWarning("Cache", "No se pudo limpiar la caché del todo: ${e.message}")
        }
    }

    // --- LOGS TEMPORALES Y ADVERTENCIAS ---
    fun logWarning(tag: String, message: String) {
        val dir = logsWarningsDir ?: return
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val dateStamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val warningFile = File(dir, "warnings_$dateStamp.log")
            
            FileWriter(warningFile, true).use { writer ->
                writer.write("[$timestamp] [$tag]: $message\n")
            }
            Log.w(TAG, "Registered warning log: [$tag] $message")
            
            // Forzar límite de espacio de 20MB max a nivel de carpeta de Warnings
            pruneDirectoryIfNeeded(dir, 20L * 1024 * 1024)
        } catch (e: Exception) {
            Log.e(TAG, "Error persistiendo warning log", e)
        }
    }

    // --- REPORTE DE CRASHEOS ---
    fun logCrash(throwable: Throwable) {
        val dir = logsCrashesDir ?: return
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS", Locale.getDefault()).format(Date())
            val crashFile = File(dir, "crash_$timestamp.log")
            
            FileWriter(crashFile).use { fw ->
                PrintWriter(fw).use { pw ->
                    pw.println("==================================================")
                    pw.println("   REGISTRO CRÍTICO DE FALLO (ActionStack Crash)  ")
                    pw.println("==================================================")
                    pw.println("Fecha y Hora: $timestamp")
                    pw.println("Android Versión: ${android.os.Build.VERSION.RELEASE} (SDK ${android.os.Build.VERSION.SDK_INT})")
                    pw.println("Hardware: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
                    pw.println("Error principal: ${throwable.localizedMessage ?: throwable.message}")
                    pw.println("Causa directa: ${throwable.cause}")
                    pw.println("--------------------------------------------------")
                    pw.println("Resumen de Pila (Stack Trace):")
                    throwable.printStackTrace(pw)
                    pw.println("==================================================")
                    pw.flush()
                }
            }
            Log.e(TAG, "Reporte de crash guardado correctamente en: ${crashFile.name}")
            
            // Forzar límite de espacio de 20MB max a nivel de carpeta de Crashes
            pruneDirectoryIfNeeded(dir, 20L * 1024 * 1024)
        } catch (e: Exception) {
            Log.e(TAG, "Fallo de respaldo al registrar el crash", e)
        }
    }

    // --- MÉTODOS DE REPLICACIÓN DE LOGS PARA EL PANEL SECRETO ---
    data class LogEntry(
        val fileName: String, 
        val lastModified: String, 
        val content: String,
        val sizeFormatted: String
    )

    fun getWarningLogs(): List<LogEntry> {
        val dir = logsWarningsDir ?: return emptyList()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dir.listFiles()?.map { file ->
            LogEntry(
                fileName = file.name,
                lastModified = sdf.format(Date(file.lastModified())),
                content = try { file.readText() } catch (e: Exception) { "Error al leer log: ${e.message}" },
                sizeFormatted = formatFileSize(file.length())
            )
        }?.sortedByDescending { it.fileName } ?: emptyList()
    }

    fun getCrashLogs(): List<LogEntry> {
        val dir = logsCrashesDir ?: return emptyList()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dir.listFiles()?.map { file ->
            LogEntry(
                fileName = file.name,
                lastModified = sdf.format(Date(file.lastModified())),
                content = try { file.readText() } catch (e: Exception) { "Error al leer log: ${e.message}" },
                sizeFormatted = formatFileSize(file.length())
            )
        }?.sortedByDescending { it.fileName } ?: emptyList()
    }

    fun clearAllLogs() {
        try {
            logsWarningsDir?.listFiles()?.forEach { it.delete() }
            logsCrashesDir?.listFiles()?.forEach { it.delete() }
            Log.d(TAG, "✓ Todos los logs de Warnings y Crashes han sido eliminados de forma segura.")
        } catch (e: Exception) {
            logWarning("System", "Error al borrar todos los logs de depuración: ${e.message}")
        }
    }

    private fun pruneDirectoryIfNeeded(dir: File, maxSizeInBytes: Long) {
        try {
            var files = dir.listFiles()?.toMutableList() ?: return
            while (getDirectorySize(dir) > maxSizeInBytes && files.isNotEmpty()) {
                files.sortBy { it.lastModified() }
                val oldest = files.removeAt(0)
                val name = oldest.name
                if (oldest.delete()) {
                    Log.d(TAG, "📁 [Poda de Logs - 20MB] Archivo viejo eliminado para ahorrar espacio: $name")
                } else {
                    break
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error podando logs en ${dir.absolutePath}", e)
        }
    }

    private fun getDirectorySize(dir: File): Long {
        var size: Long = 0
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) getDirectorySize(file) else file.length()
        }
        return size
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(1024.0)).toInt()
        val pre = "KMGTPE"[exp - 1]
        return String.format(Locale.US, "%.1f %cB", bytes / Math.pow(1024.0, exp.toDouble()), pre)
    }
}
