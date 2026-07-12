package com.bbt.client

import com.bbt.FuckJose.MOD_ID
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import java.io.File

data class ModConfig(
    var autoOnJoin: Boolean = false,
    var displayPrefix: String = "§d[FuckJose]§r",
    var debugMode: Boolean = false
)

object ModConfigManager {
    private val logger = LoggerFactory.getLogger(MOD_ID)
    private val configFile = File(File(System.getProperty("user.home"), ".fuckjose"), "config.json")
    private val gson = GsonBuilder().setPrettyPrinting().create()

    // Global access to your mod configuration
    var config = ModConfig()

    init {
        load()
    }

    fun load() {
        try {
            if (configFile.exists()) {
                val jsonString = configFile.readText()
                config = gson.fromJson(jsonString, ModConfig::class.java)
                logger.info("Configuration loaded from disk.")
            } else {
                save() // Create default config if missing
            }
        } catch (e: Exception) {
            logger.error("Failed to load config", e)
        }
    }

    fun save() {
        try {
            configFile.parentFile.mkdirs()
            val jsonString = gson.toJson(config)
            configFile.writeText(jsonString)
            logger.info("Config saved.")
        } catch (e: Exception) {
            logger.error("Failed to save config", e)
        }
    }
}