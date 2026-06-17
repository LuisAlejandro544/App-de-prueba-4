package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import java.lang.ref.WeakReference

class MyAccessibilityService : AccessibilityService() {

    interface NotificationListener {
        fun onNotificationReceived(packageName: String, title: String, text: String)
    }

    companion object {
        private var instanceRef = WeakReference<MyAccessibilityService>(null)

        private val notificationListeners = mutableListOf<NotificationListener>()

        val isActive: Boolean
            get() = instanceRef.get() != null

        fun registerNotificationListener(listener: NotificationListener) {
            synchronized(notificationListeners) {
                notificationListeners.add(listener)
            }
        }

        fun unregisterNotificationListener(listener: NotificationListener) {
            synchronized(notificationListeners) {
                notificationListeners.remove(listener)
            }
        }

        fun performGlobalAction(actionId: Int): Boolean {
            val service = instanceRef.get()
            return if (service != null) {
                service.performGlobalAction(actionId)
            } else {
                false
            }
        }
    }

    private var shakeSensorHelper: ShakeSensorHelper? = null
    private var volumeButtonsTriggerHelper: VolumeButtonsTriggerHelper? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        instanceRef = WeakReference(this)
        shakeSensorHelper = ShakeSensorHelper(this).apply {
            startListening()
        }
        volumeButtonsTriggerHelper = VolumeButtonsTriggerHelper(this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        shakeSensorHelper?.stopListening()
        shakeSensorHelper = null
        volumeButtonsTriggerHelper = null
        instanceRef = WeakReference(null)
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        shakeSensorHelper?.stopListening()
        shakeSensorHelper = null
        volumeButtonsTriggerHelper = null
        instanceRef = WeakReference(null)
        super.onDestroy()
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        val keyCode = event?.keyCode
        val action = event?.action
        if (action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                val handled = volumeButtonsTriggerHelper?.onVolumeUpPressed() ?: false
                if (handled) return true
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                val handled = volumeButtonsTriggerHelper?.onVolumeDownPressed() ?: false
                if (handled) return true
            }
        }
        return super.onKeyEvent(event)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event != null && event.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: ""
            var title = ""
            var text = ""

            val parcelableData = event.parcelableData
            if (parcelableData is android.app.Notification) {
                val extras = parcelableData.extras
                title = extras?.getCharSequence(android.app.Notification.EXTRA_TITLE)?.toString() ?: ""
                text = extras?.getCharSequence(android.app.Notification.EXTRA_TEXT)?.toString() ?: ""
            }

            if (text.isEmpty()) {
                val textList = event.text ?: emptyList()
                text = textList.joinToString(" ")
            }

            synchronized(notificationListeners) {
                notificationListeners.forEach { listener ->
                    try {
                        listener.onNotificationReceived(packageName, title, text)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
        // Interrupción del servicio
    }
}
