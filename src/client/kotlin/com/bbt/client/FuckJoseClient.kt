package com.bbt.client

import com.bbt.client.chatCommands.BasicKuudraPV
import com.bbt.client.ModConfigManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
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