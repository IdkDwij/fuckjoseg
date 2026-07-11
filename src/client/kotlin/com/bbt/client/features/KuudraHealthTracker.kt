package com.bbt.client.features

import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting

object KuudraHealthTracker {
    private var isStunned = false
    private var alertTriggered = false
    private const val HP_THRESHOLD = 25000

    fun stop() {

    }
    fun handleActionbar(message: Component) {
        if (!isStunned || alertTriggered) return

        val content: String = message.getString()
        if (content.contains("Kuudra")) {
            val currentHP = parseHealth(content)

            if (currentHP > 0 && currentHP <= HP_THRESHOLD) {
                triggerStunAlert(currentHP)
                alertTriggered = true // Avoid spamming the alert
            }
        }
    }

    private fun parseHealth(rawText: String): Int {
        try {
            // Strip commas and clean string to extract numeric HP
            // Adjust regex depending on Hypixel's precise text layout
            var clean = rawText.replace("[^0-9/]".toRegex(), "")
            if (clean.contains("/")) {
                clean =
                    clean.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] // Target current health
            }
            return clean.toInt()
        } catch (e: NumberFormatException) {
            return -1
        }
    }

    private fun triggerStunAlert(hp: Int) {
        val msg = Component.literal("[ALERT] KUUDRA HP IS $hp - DPS FAST!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
        net.minecraft.client.Minecraft.getInstance().execute {
//            AlertOverlay.show(msg, 60) // 60 ticks = 3 seconds
        }
    }
}