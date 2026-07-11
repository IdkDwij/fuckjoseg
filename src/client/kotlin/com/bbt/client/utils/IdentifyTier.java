package com.bbt.client.utils;

//Taken from Phantomaddons cause im pretty stupid

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public final class IdentifyTier {
    private static int kuudraTier = 0;
    private static int lastKnownTier = 0;
    private static int tickCounter = 0;
    private static boolean inKuudraHollow = false;
    private static boolean inForgottenSkull = false;

    private IdentifyTier() {
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register((ClientTickEvents.EndTick)(client) -> {
            if (client.player != null && client.level != null) {
                if (++tickCounter >= 20) {
                    tickCounter = 0;
                    detectAll(client);
                }
            }
        });
    }

    private static void detectAll(Minecraft client) {
        inKuudraHollow = false;
        inForgottenSkull = false;
        kuudraTier = 0;

        for(String line : getSidebarLines(client)) {
            String clean = line.replaceAll("§[0-9a-fk-orA-FK-OR]", "").trim();
            if (clean.contains("Kuudra's Hollow")) {
                inKuudraHollow = true;
            }
            if (clean.contains("Forgotten Skull")) {
                inForgottenSkull = true;
            }

            if (inKuudraHollow && kuudraTier == 0) {
                if (clean.contains("(T1)")) {
                    kuudraTier = 1;
                } else if (clean.contains("(T2)")) {
                    kuudraTier = 2;
                } else if (clean.contains("(T3)")) {
                    kuudraTier = 3;
                } else if (clean.contains("(T4)")) {
                    kuudraTier = 4;
                } else if (clean.contains("(T5)")) {
                    kuudraTier = 5;
                }
            }
        }

        if (kuudraTier > 0) {
            lastKnownTier = kuudraTier;
        }

    }

    private static List<String> getSidebarLines(Minecraft client) {
        List<String> lines = new ArrayList();
        if (client.level == null) {
            return lines;
        } else {
            Scoreboard scoreboard = client.level.getScoreboard();
            Objective sidebar = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
            if (sidebar == null) {
                return lines;
            } else {
                for(PlayerScoreEntry entry : scoreboard.listPlayerScores(sidebar)) {
                    String holderName = entry.ownerName().getString();
                    PlayerTeam team = scoreboard.getPlayersTeam(holderName);
                    if (team != null) {
                        String var10001 = team.getPlayerPrefix().getString();
                        lines.add(var10001 + team.getPlayerSuffix().getString());
                    } else {
                        lines.add(holderName);
                    }
                }

                return lines;
            }
        }
    }

    public static int getTier() {
        return kuudraTier;
    }

    public static int getLastKnownTier() {
        return lastKnownTier;
    }

    public static boolean isInKuudraHollow() {
        return inKuudraHollow;
    }

    public static boolean isInForgottenSkull() {
        return inForgottenSkull;
    }

    public static void reset() {
        kuudraTier = 0;
        tickCounter = 0;
    }
}
