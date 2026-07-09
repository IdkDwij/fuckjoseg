package com.bbt.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientChatReceivedMixin {

    @Inject(method = "onChatMessage", at = @At("HEAD"))
    private void onChatMessageReceived(ChatMessageS2CPacket packet, CallbackInfo ci) {
        // 1. Get the actual message string out of the modern packet content
        String messageContent = packet.content().toLowerCase();

        // 2. Define the keyword or phrase you want to listen for
        String targetKeyword = "expired";

        // 3. Check if the message contains your keyword
        if (messageContent.contains(targetKeyword)) {
            // Trigger the alert on the main thread safely
            MinecraftClient.getInstance().execute(() -> {
                triggerAlertNotification(packet.content());
            });
        }
    }

    @Unique
    private void triggerAlertNotification(String matchedMessage) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Send a private, client-side system chat warning using modern Text builders
        client.player.sendMessage(
                Text.literal("⚠️ [ALERT] Found keyword in chat: " + matchedMessage)
                        .withColor(0xFFFF5555) // Red color
        );
    }
}
