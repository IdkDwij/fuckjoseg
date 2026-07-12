package com.bbt.client

import com.bbt.client.chatCommands.BasicKuudraPV
import com.bbt.client.ModConfigManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import com.mojang.brigadier.arguments.StringArgumentType // Import brigadier string parser
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object FuckJoseClient : ClientModInitializer {
    override fun onInitializeClient() {
        BasicKuudraPV.register()
        registerCommands()
    }

    private fun registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommands.literal("kuudratoggle")
                    .executes { context ->
                        val currentConfig = ModConfigManager.config
                        currentConfig.autoOnJoin = !currentConfig.autoOnJoin

                        // Push update straight to file manager
                        ModConfigManager.save()

                        val statusText = if (currentConfig.autoOnJoin) "§aENABLED" else "§cDISABLED"

                        Minecraft.getInstance().player?.sendSystemMessage(
                            Component.literal("${currentConfig.displayPrefix} Auto-Kuudra PV on party join is now $statusText")
                        )
                        1
                    }
            )
            dispatcher.register(
                ClientCommands.literal("modname")
                    .then(
                        ClientCommands.argument("newName", StringArgumentType.greedyString())
                            .executes { context ->
                                // Grab the typed input string
                                val newName = StringArgumentType.getString(context, "newName")

                                // Format it into standard bracket styling
                                val formattedPrefix = "[$newName]"

                                // Save it globally to configuration storage
                                ModConfigManager.config.displayPrefix = formattedPrefix
                                ModConfigManager.save()

                                Minecraft.getInstance().player?.sendSystemMessage(
                                    Component.literal("$formattedPrefix Mod chat prefix has been updated successfully!")
                                )
                                1
                            }
                    )
            )
            /*
            dispatcher.register(
                ClientCommands.literal("kuudra")
                    .executes { context ->
                        // code
                        1
                    }
            )
            */
        }
    }
}