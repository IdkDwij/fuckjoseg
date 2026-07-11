package com.bbt.client.chatCommands

import com.bbt.FuckJose.MOD_ID
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import net.minecraft.client.Minecraft
import com.google.gson.JsonParser
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.coroutines.*

object BasicKuudraPV {

    private val client: HttpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.ALWAYS)
        .build()

    val logger: Logger? = getLogger(MOD_ID)
    val MCClient = Minecraft.getInstance()
    private val apiKey: String = System.getenv("HYPIXEL_API_KEY") ?: "YOUR_API_KEY_HERE"

    // Asynchronous worker scope for background HTTP tasks
    private val modScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun register() {
        ClientReceiveMessageEvents.CHAT.register { message, playerChatMessage, gameProfile, chatType, instant ->
            val text = message.string
            val regex = Regex("!kuudra\\s+(\\S+)")
            val match = regex.find(text)

            if (match != null) {
                // FIXED: Explicitly pull index 1 from groupValues to get the plain-text String name
                val targetPlayerName = match.groupValues[1].trim()
                logger?.info("Intercepted player name: $targetPlayerName")

                // Spin up network lookups asynchronously
                modScope.launch {
                    val uuidOrError = getUuidFromUsername(targetPlayerName)

                    // Safely hand task instructions back to the Minecraft engine loop thread
                    MCClient.execute {
                        fetchKuudra(uuidOrError)
                    }
                }
            }
        }
    }

    fun fetchKuudra(uuidOrError: String) {
        if (uuidOrError.startsWith("Error")) {
            MCClient.player?.connection?.sendCommand("pc [Mod] $uuidOrError")
            return
        }
        
        logger?.info("Getting Kuudra data for UUID: $uuidOrError")

        // Fetch Hypixel stats asynchronously
        modScope.launch {
            val stats = getKuudraStats(uuidOrError)
            
            // Switch back to main thread to send command
            MCClient.execute {
                MCClient.player?.connection?.sendCommand("pc [Mod] $stats")
            }
        }
    }

    suspend fun getKuudraStats(uuid: String): String {
        try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.hypixel.net/skyblock/profiles?key=$apiKey&uuid=$uuid"))
                .GET()
                .build()

            val response = withContext(Dispatchers.IO) {
                client.send(request, HttpResponse.BodyHandlers.ofString())
            }

            if (response.statusCode() != 200) {
                return "Error: HTTP ${response.statusCode()}"
            }

            val jsonObject = JsonParser.parseString(response.body()).asJsonObject
            
            if (!jsonObject.get("success").asBoolean) {
                return "Error: No SkyBlock profiles found"
            }

            // Get the first/selected profile
            val profiles = jsonObject.getAsJsonArray("profiles")
            if (profiles.size() == 0) {
                return "Error: No profiles available"
            }

            val profile = profiles[0].asJsonObject
            val members = profile.getAsJsonObject("members")
            val playerProfile = members.getAsJsonObject(uuid)
            
            // Get Kuudra stats
            val dungeons = playerProfile.getAsJsonObject("dungeons")
            val kuudra = dungeons.getAsJsonObject("kuudra")
            
            val completions = kuudra.get("completed_runs").asInt
            val bestScore = kuudra.get("best_score").asInt
            
            return "Kuudra - Completions: $completions, Best Score: $bestScore"

        } catch (e: Exception) {
            logger?.error("Failed to get Kuudra stats for $uuid", e)
            return "Error: ${e.message}"
        }
    }

    suspend fun getUuidFromUsername(username: String): String {
        try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/$username"))  // Add username to URL
                .GET()
                .build()

            val response = withContext(Dispatchers.IO) {
                client.send(request, HttpResponse.BodyHandlers.ofString())
            }

            if (response.statusCode() == 204) {
                return "Mojang Error: Username '$username' was not found"
            }

            if (response.statusCode() != 200) {
                return "Mojang Error: HTTP ${response.statusCode()}"
            }

            val jsonObject = JsonParser.parseString(response.body()).asJsonObject
            return jsonObject.get("id").asString

        } catch (e: Exception) {
            logger?.error("Failed to get UUID for $username", e)
            return "Error: ${e.message}"
        }
    }
}
