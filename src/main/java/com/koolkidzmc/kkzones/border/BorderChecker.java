package com.koolkidzmc.kkzones.border;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.utils.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class BorderChecker {
    public static void init(KKZones plugin) {
        plugin.getLogger().info("Setting Messsage Channels...");
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getLogger().info("Starting Asynchronous Tasks...");
        try {
            TaskManager.Async.runTask(checkLoc, 5);
            plugin.getLogger().info("Asynchronous Tasks Started!");
        } catch (Exception e) {
            plugin.getLogger().warning("Error while starting Asynchronous Tasks: " + e);
        }
    }

    static Runnable checkLoc = () -> {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {

        }
    };
}
