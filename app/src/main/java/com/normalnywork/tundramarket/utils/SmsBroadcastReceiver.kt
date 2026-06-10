package com.normalnywork.tundramarket.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.normalnywork.tundramarket.data.IncomingSmsOrderHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SmsBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val smsOrderHandler: IncomingSmsOrderHandler by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val senderPhone = messages.firstOrNull()?.originatingAddress
        val messageBody = messages.joinToString(separator = "") { message ->
            message.messageBody.orEmpty()
        }
        if (messageBody.isBlank()) return

        val pendingResult = goAsync()
        receiverScope.launch {
            runCatching {
                smsOrderHandler.handle(
                    senderPhone = senderPhone,
                    message = messageBody,
                )
            }.also {
                pendingResult.finish()
            }
        }
    }

    private companion object {

        val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}
