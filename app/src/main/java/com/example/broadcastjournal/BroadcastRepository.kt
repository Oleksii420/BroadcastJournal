package com.example.broadcastjournal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class BroadcastRepository(private val context: Context) {

    fun observeEvents(): Flow<BroadcastEvent> = callbackFlow {

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent ?: return
                trySend(intent.toEvent())
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
        }

        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    @Suppress("DEPRECATION")
    private fun Intent.toEvent(): BroadcastEvent {
        val type = when (action) {
            Intent.ACTION_POWER_CONNECTED -> "Зарядний пристрій підключено"
            Intent.ACTION_POWER_DISCONNECTED -> "Зарядний пристрій відключено"
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> "Режим «У літаку» змінено"
            Intent.ACTION_BATTERY_LOW -> "Низький заряд батареї"
            Intent.ACTION_BATTERY_OKAY -> "Заряд батареї відновлено"
            else -> action ?: "Невідома подія"
        }

        val now = Date()
        val extrasText = extras?.keySet()
            ?.joinToString("\n") { key -> "• $key = ${extras?.get(key)}" }
            ?: "немає"

        return BroadcastEvent(
            id = UUID.randomUUID().toString(),
            type = type,
            timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now),
            details = "Action: $action\n" +
                    "Точний час: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS", Locale.getDefault()).format(now)}\n" +
                    "Extras:\n$extrasText"
        )
    }
}