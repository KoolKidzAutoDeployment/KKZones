package com.koolkidzmc.kkzones.utils;

import com.koolkidzmc.kkzones.KKZones;

public class TaskManager {

    public class Async {
        public static void run(Runnable runnable) {
            KKZones.getPlugin(KKZones.class).getServer().getScheduler().runTaskAsynchronously(KKZones.getPlugin(KKZones.class), runnable);
        }

        public static void runTask(Runnable runnable, long interval) {
            KKZones.getPlugin(KKZones.class).getServer().getScheduler().runTaskTimerAsynchronously(KKZones.getPlugin(KKZones.class), runnable, 0L, interval);
        }

        public static void runLater(Runnable runnable, long delay) {
            KKZones.getPlugin(KKZones.class).getServer().getScheduler().runTaskLaterAsynchronously(KKZones.getPlugin(KKZones.class), runnable, delay);
        }
    }
}
