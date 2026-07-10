package com.bbt.client.Features.phase;

//Taken from Phantomaddons cause im pretty stupid

import com.kuudrahelper.KuudraHelperMod;
import com.kuudrahelper.utils.KuudraTierDetector;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KuudraPhaseTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(KuudraPhaseTracker.class);
    private static Phase currentPhase;
    private static boolean runActive;

    public static void init() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((ClientReceiveMessageEvents.AllowGame)(text, overlay) -> {
            handle(text.getString());
            return true;
        });
        ClientReceiveMessageEvents.CHAT.register((ClientReceiveMessageEvents.Chat)(text, signed, sender, params, ts) -> handle(text.getString()));
        ClientTickEvents.END_CLIENT_TICK.register((ClientTickEvents.EndTick)(client) -> {
            if (client.player != null) {
                checkBossYTransition();
            }
        });
    }

    public static String stripFormatting(String msg) {
        return msg.replaceAll("§[0-9a-fk-or]", "");
    }

    private static void handle(String raw) {
        String msg = stripFormatting(raw);
        if (msg.contains("Kuudra") || msg.contains("Ballista") || msg.contains("supplies") || msg.contains("fish up") || msg.contains("eaten") || msg.contains("pods") || msg.contains("Elle") || msg.contains("begin")) {
            LOGGER.info("[PhaseTracker] msg='{}'", msg);
        }

        if (msg.contains("Talk with me to begin")) {
            runActive = false;
            setPhase(KuudraPhaseTracker.Phase.NONE);
        } else if (msg.contains("I will go and fish up Kuudra")) {
            runActive = true;
            setPhase(KuudraPhaseTracker.Phase.SUPPLIES);
        } else if (msg.contains("Great work collecting my supplies")) {
            setPhase(KuudraPhaseTracker.Phase.BUILD);
        } else if (msg.contains("The Ballista is finally ready")) {
            int tier = KuudraTierDetector.getTier();
            setPhase(tier != 1 && tier != 2 ? KuudraPhaseTracker.Phase.EATEN : KuudraPhaseTracker.Phase.BOSS);
        } else if (msg.contains("has been eaten by Kuudra!")) {
            runActive = true;
            setPhase(KuudraPhaseTracker.Phase.STUN);
        } else if (msg.contains("destroyed one of Kuudra's pods!")) {
            runActive = true;
            setPhase(KuudraPhaseTracker.Phase.DPS);
        } else if (!msg.contains("SURELY THAT'S IT") && !msg.contains("POW! SURELY")) {
            if (msg.contains("KUUDRA DOWN") || msg.contains("DEFEAT KUUDRA") || msg.contains("KUUDRA HAS BEEN DEFEATED")) {
                runActive = false;
                setPhase(KuudraPhaseTracker.Phase.END);
            }

        } else {
            runActive = true;
            setPhase(KuudraPhaseTracker.Phase.SKIP);
            checkBossYTransition();
        }
    }

    private static void checkBossYTransition() {
        if (currentPhase == KuudraPhaseTracker.Phase.SKIP) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                if (mc.player.getY() < (double)10.0F) {
                    setPhase(KuudraPhaseTracker.Phase.BOSS);
                }

            }
        }
    }

    private static void setPhase(Phase newPhase) {
        if (newPhase != currentPhase) {
            LOGGER.info("[PhaseTracker] phase {} -> {}", currentPhase, newPhase);
            currentPhase = newPhase;
            KuudraPhaseEvents.onPhaseChanged(newPhase);
        }
    }

    public static void reset() {
        currentPhase = KuudraPhaseTracker.Phase.NONE;
    }

    public static void forcePhase(Phase phase) {
        runActive = phase != KuudraPhaseTracker.Phase.NONE && phase != KuudraPhaseTracker.Phase.END;
        currentPhase = phase;
        KuudraPhaseEvents.onPhaseChanged(phase);
    }

    public static Phase getPhase() {
        return currentPhase;
    }

    public static boolean is(Phase phase) {
        return currentPhase == phase;
    }

    public static boolean isRunActive() {
        return runActive;
    }

    static {
        currentPhase = KuudraPhaseTracker.Phase.NONE;
        runActive = false;
    }

    public static enum Phase {
        NONE,
        SUPPLIES,
        BUILD,
        EATEN,
        STUN,
        DPS,
        SKIP,
        BOSS,
        END;

        // $FF: synthetic method
        private static Phase[] $values() {
            return new Phase[]{NONE, SUPPLIES, BUILD, EATEN, STUN, DPS, SKIP, BOSS, END};
        }
    }
}
