package com.koolkidzmc.kkzones.border;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.utils.TaskManager;
import org.bukkit.entity.Player;

import java.nio.channels.AsynchronousCloseException;

public class BorderChecker {
    public static void init(KKZones plugin) {
        plugin.getLogger().info("Starting Asynchronous Tasks...");
        try {
            TaskManager.Async.runTask(checkLoc, 5);
            plugin.getLogger().info("Asynchronous Tasks Started!");
        } catch (Exception e) {
            plugin.getLogger().warning("Error while starting Asynchronous Tasks: " + e);
        }
    }

    static Runnable checkLoc = () -> {

    };
}
