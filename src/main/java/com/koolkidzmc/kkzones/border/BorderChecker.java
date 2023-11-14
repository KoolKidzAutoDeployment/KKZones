package com.koolkidzmc.kkzones.border;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.utils.TaskManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class BorderChecker {
    static KKZones plugin;
    public void init(KKZones plugin) {
        BorderChecker.plugin = plugin;
        plugin.getLogger().info("Setting Messsage Channels...");
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        try {
            TaskManager.Async.runTask(checkLoc, 5);
        } catch (Exception e) {
            plugin.getLogger().warning("Error while starting Asynchronous Task [checkLoc]: " + e);
        }
    }

    static Runnable checkLoc = () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().getBlockX() >= 9997.00) {
                player.sendMessage("Sending you to " + plugin.getConfig().getString("servers.east"));
            }
            if (player.getLocation().getBlockX() >= -9997.00) {
                player.sendMessage("Sending you to " + plugin.getConfig().getString("servers.west"));
            }
            if (player.getLocation().getBlockZ() >= 9997.00) {
                player.sendMessage("Sending you to " + plugin.getConfig().getString("servers.south"));
            }
            if (player.getLocation().getBlockZ() >= 9997.00) {
                player.sendMessage("Sending you to " + plugin.getConfig().getString("servers.north"));
            }
        }
    };
}
