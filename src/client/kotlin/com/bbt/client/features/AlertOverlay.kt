package com.bbt.client.features

//import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
//import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
//import net.minecraft.client.Minecraft
//import net.minecraft.client.gui.GuiGraphicsExtractor
//import net.minecraft.client.render.DeltaTracker
//import net.minecraft.network.chat.Component
//import net.minecraft.resources.Identifier
//import net.minecraft.resources.ResourceLocation
//
//object AlertOverlay {
//    @Volatile
//    private var message: Component? = null
//
//    @Volatile
//    private var ticksLeft = 0
//
//    fun show(msg: Component, durationTicks: Int) {
//        message = msg
//        ticksLeft = durationTicks
//    }
//
//    init {
//        HudElementRegistry.attachElementBefore(
//            VanillaHudElements.CHAT,
//            Identifier.fromNamespaceAndPath("fuckjose", "alert_overlay")
//        ) { graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker ->
//            val client = Minecraft.getInstance()
//            val msg = message ?: return@attachElementBefore
//            if (ticksLeft <= 0) {
//                message = null
//                return@attachElementBefore
//            }
//
//            val text = msg.getString()
//            val x = (client.window.scaledWidth - client.font.width(text)) / 2
//            val y = 20
//            graphics.drawString(client.font, text, x, y, 0xFFFF5555.toInt())
//            ticksLeft--
//        }
//    }
//}
