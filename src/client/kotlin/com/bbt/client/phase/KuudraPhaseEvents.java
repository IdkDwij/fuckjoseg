package com.bbt.client.phase;


import net.minecraft.client.Minecraft;

public final class KuudraPhaseEvents {
   public static void onPhaseChanged(KuudraPhaseTracker.Phase phase) {
      Minecraft client = Minecraft.getInstance();
      if (client != null) {
         client.execute(() -> {
            switch (phase) {
               case SUPPLIES:
//                  HideArmorStands.activate();
//                  SupplyWaypointTracker.onSuppliesStart();
//                  NoPreAnnounce.onSuppliesStart();
//                  EtherwarpWaypointManager.onSuppliesStart();
//                  SupplyProgressHud.onSuppliesStart();
//                  BuildProgressHud.reset();
//                  AnnounceFresh.reset();
//                  PearlWaypointManager.reset();
//                  AutoGFS.stop();
//                  Phase2BuildTracker.stop();
//                  PhaseLogger.end();
//                  FastDpsWarning.onDpsEnd();
//                  SoloDetector.onPhaseEnd();
//                  SoloDetector.onPhaseStart();
//                  KuudraSplitTimer.onSuppliesStart();
                  break;
               case BUILD:
//                  SupplyWaypointTracker.reset();
//                  EtherwarpWaypointManager.reset();
//                  SupplyProgressHud.reset();
//                  BuildProgressTracker.start();
//                  BuildProgressHud.onBuildStart();
//                  AnnounceFresh.onBuildStart();
//                  Phase2BuildTracker.start();
//                  PearlWaypointManager.reset();
//                  KuudraSplitTimer.onBuildStart();
                  break;
               case EATEN:
//                  BuildProgressTracker.stop();
//                  BuildProgressHud.reset();
//                  PhaseLogger.resetTick();
//                  RoleManager.reset();
//                  if (KuudraConfig.isAutoMode()) {
//                     RoleManager.resolveAutoRole(client);
//                  } else {
//                     RoleManager.setManualRole(KuudraConfig.getRoleMode());
//                  }
//
//                  AutoGFS.queueCommand();
//                  AutoGFS.start(KuudraConfig.getDpsRefillAmount());
//                  PhaseLogger.begin();
//                  KuudraSplitTimer.onEatenStart();
                  break;
               case STUN:
//                  RendDamage.onKillPhaseStart();
//                  RendTracker.onKillPhaseStart();
//                  KuudraSplitTimer.onStunStart();
                  break;
               case DPS:
//                  RendDamage.onKillPhaseStart();
//                  RendTracker.onKillPhaseStart();
//                  FastDpsWarning.onDpsStart();
//                  KuudraSplitTimer.onDpsStart();
                  break;
               case SKIP:
//                  BuildProgressTracker.stop();
//                  BuildProgressHud.reset();
//                  AnnounceFresh.reset();
//                  AutoGFS.stop();
//                  Phase2BuildTracker.stop();
//                  PhaseLogger.end();
//                  RoleManager.reset();
//                  FastDpsWarning.onDpsEnd();
//                  PearlWaypointManager.reset();
//                  KuudraSplitTimer.onSkipStart();
                  break;
               case BOSS:
//                  RendDamage.onBossPhaseStart();
//                  RendTracker.onBossPhaseStart();
//                  KuudraSplitTimer.onBossStart();
                  break;
               case END:
//                  AutoRequeue.trigger();
//                  RendDamage.reset();
//                  RendTracker.reset();
//                  HideArmorStands.deactivate();
//                  BuildProgressTracker.stop();
//                  BuildProgressHud.reset();
//                  AnnounceFresh.reset();
//                  SupplyProgressHud.reset();
//                  AutoGFS.stop();
//                  Phase2BuildTracker.stop();
//                  PhaseLogger.end();
//                  RoleManager.reset();
//                  FastDpsWarning.onDpsEnd();
//                  PearlWaypointManager.reset();
//                  KuudraSplitTimer.onEndStart();
            }

         });
      }
   }
}
