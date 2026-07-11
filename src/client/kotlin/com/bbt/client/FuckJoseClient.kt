package com.bbt.client

import com.bbt.client.chatCommands.BasicKuudraPV
import net.fabricmc.api.ClientModInitializer



object FuckJoseClient : ClientModInitializer {
	override fun onInitializeClient() {
		BasicKuudraPV.register()
	}
}