package com.bbt.client.chatCommands

import com.bbt.FuckJose.MOD_ID
import com.bbt.client.ModConfigManager
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import net.minecraft.client.Minecraft
import com.google.gson.JsonParser
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader

object BasicKuudraPV {

    val logger: Logger? = getLogger(MOD_ID)
    val MCClient = Minecraft.getInstance()
    private val modScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun register() {
        ClientReceiveMessageEvents.GAME.register { message, overlay ->
            val rawText = message.string
            val text = rawText.replace(Regex("§[0-9a-fk-orxX]"), "")

            val commandRegex = Regex("^\\s*Party\\s+>\\s+[^:]+:\\s*!kuudra\\s+(\\S+)", RegexOption.IGNORE_CASE)
            val commandMatch = commandRegex.find(text)

            if (commandMatch != null) {
                val targetPlayerName = commandMatch.groupValues[1].trim()
                fetchKuudra(targetPlayerName)
                return@register
            }

            if (ModConfigManager.config.autoOnJoin) {
                val joinRegex = Regex("^\\s*(?:\\[[^\\]]+\\]\\s+)?(\\w+)\\s+joined\\s+the\\s+party\\.")
                val joinMatch = joinRegex.find(text)

                if (joinMatch != null) {
                    val joinedPlayerName = joinMatch.groupValues[1].trim()
                    fetchKuudra(joinedPlayerName)
                }
            }
        }
    }

    fun fetchKuudra(playerName: String) {
        logger?.info("Requesting Kuudra data from Worker for: $playerName")

        modScope.launch {
            val statsJson = getKuudraStats(playerName)

            // Format the display string based on Worker JSON payload
            val outputMessage = if (statsJson != null && statsJson.get("success")?.asBoolean == true) {
                val magicalPower = statsJson.get("magicalPower").asLong
                val kuudraObj = statsJson.getAsJsonObject("kuudraCompletions")
                val infernalRuns = kuudraObj.get("infernal").asInt
                "Runs: $infernalRuns | MP: $magicalPower"
            } else {
                "Error: Could not retrieve stats from API backend."
            }

            // Step 3: Switch back to main thread to submit game macro command safely
            MCClient.execute {
                MCClient.player?.connection?.sendCommand("pc [$MOD_ID] $outputMessage")
            }
        }
    }

    private suspend fun getKuudraStats(playerName: String): JsonObject? = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://kuudra-backend.idkdwij.workers.dev/profile?name=$playerName"
            val url = URL(urlString)

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // FIXED: Removed invalid parentheses from connection.inputStream
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                return@withContext JsonParser.parseString(response.toString()).asJsonObject
            } else {
                logger?.error("Backend API returned error code: $responseCode")
            }
        } catch (e: Exception) {
            logger?.error("Failed connecting to Cloudflare Worker", e)
        }
        return@withContext null
    }
}
//

//    suspend fun getKuudraStats(uuid: String): String {
//        try {
//            val currentKey = apiKey
//            if (currentKey.isEmpty() || currentKey == "PASTE_YOUR_HYPIXEL_API_KEY_HERE") {
//                return "Error: No API key set. Use /api <key>"
//            }
//
//            logger?.info("API Key loaded: ${currentKey.take(4)}...")
//            logger?.info("Requesting Hypixel API for UUID: $uuid")
//
//            // Hypixel stores player UUIDs without dashes inside the profile structure
//            val cleanUuid = uuid.replace("-", "")
//
//            val request = HttpRequest.newBuilder()
//                .uri(URI.create("https://api.hypixel.net/v2/skyblock/profiles?uuid=$cleanUuid"))
//                .header("API-Key", currentKey) // Pass key in the header
//                .header("User-Agent", "MinecraftMod/1.0")
//                .GET()
//                .build()
//
//            val response = withContext(Dispatchers.IO) {
//                client.send(request, HttpResponse.BodyHandlers.ofString())
//            }
//
//            logger?.info("Hypixel API Response Code: ${response.statusCode()}")
//
//            if (response.statusCode() != 200) {
//                logger?.warn("Hypixel API Error Response: ${response.body().take(200)}")
//                return "Error: HTTP ${response.statusCode()}"
//            }
//
//            val jsonObject = JsonParser.parseString(response.body()).asJsonObject
//
//            if (!jsonObject.get("success").asBoolean) {
//                return "Error: Request failed on Hypixel API"
//            }
//
//            // Get the first profile
//            val profiles = jsonObject.getAsJsonArray("profiles")
//            if (profiles == null || profiles.size() == 0) {
//                return "Error: No SkyBlock profiles found for this player"
//            }
//
//            // Target the active profile if flagged, otherwise fall back to the first profile in the list
//            var activeProfile = profiles[0].asJsonObject
//            for (p in profiles) {
//                val pObj = p.asJsonObject
//                if (pObj.has("selected") && pObj.get("selected").asBoolean) {
//                    activeProfile = pObj
//                    break
//                }
//            }
//
//            val members = activeProfile.getAsJsonObject("members")
//            if (!members.has(cleanUuid)) {
//                return "Error: Member data missing from profile"
//            }
//            val playerProfile = members.getAsJsonObject(cleanUuid)
//
//            var magicalPower = 0
//            if (playerProfile.has("accessory_bag_storage")) {
//                val accessoryBag = playerProfile.getAsJsonObject("accessory_bag_storage")
//                if (accessoryBag.has("magical_power")) {
//                    magicalPower = accessoryBag.get("magical_power").asInt
//                }
//            }
//
//            if (!playerProfile.has("nether_island_player_data")) {
//                return "Runs: 0 (No Crimson Isle data) | MP:$magicalPower"
//            }
//
//            val netherData = playerProfile.getAsJsonObject("nether_island_player_data")
//            if (!netherData.has("kuudra_completed_tiers")) {
//                return "Runs: 0 | MP:$magicalPower"
//            }
//
//            val kuudraTiers = netherData.getAsJsonObject("kuudra_completed_tiers")
//
//            // Extract Infernal tier count (returns 0 if the key is absent)
//            val infernalRuns = if (kuudraTiers.has("infernal")) kuudraTiers.get("infernal").asInt else 0
//            val cuteName = if (activeProfile.has("cute_name")) activeProfile.get("cute_name").asString else "Unknown"
//            return "Runs: $infernalRuns | MP: $magicalPower"
//
//        } catch (e: Exception) {
//            logger?.error("Failed to get Kuudra stats for $uuid", e)
//            return "Hypixel Error: ${e.message}"
//        }
//    }

//    suspend fun getUuidFromUsername(username: String): String {
//        try {
//            val request = HttpRequest.newBuilder()
//                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/$username"))  // Add username to URL
//                .GET()
//                .build()
//
//            val response = withContext(Dispatchers.IO) {
//                client.send(request, HttpResponse.BodyHandlers.ofString())
//            }
//
//            if (response.statusCode() == 204) {
//                return "Mojang Error: Username '$username' was not found"
//            }
//
//            if (response.statusCode() != 200) {
//                return "Mojang Error: HTTP ${response.statusCode()}"
//            }
//
//            val jsonObject = JsonParser.parseString(response.body()).asJsonObject
//            return jsonObject.get("id").asString
//
//        } catch (e: Exception) {
//            logger?.error(" Failed to get UUID for $username", e)
//            return "Error: ${e.message}"
//        }
//    }