package com.koolkidzmc.kkzones.gui.serverinfo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.gui.ServerSelectorGUI;
import com.koolkidzmc.kkzones.utils.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;

public class ServerPinger {
    private KKZones plugin;
    public void init(KKZones plugin) {
        this.plugin = plugin;
        try {
            startMonitoring();
        } catch (Exception e) {
            plugin.getLogger().warning("Error while starting Asynchronous Task [startMonitoring]: " + e);
        }
        try {
            TaskManager.Async.runTask(pingServers, 20);
        } catch (Exception e) {
            plugin.getLogger().warning("Error while starting Asynchronous Task [pingServers]: " + e);
        }
    }
    public void startMonitoring() {
        long startTime = System.currentTimeMillis();

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getThisServer();
            double tps = Math.min(plugin.getServer().getTPS()[0], 20.0);
            long lastHeartBeat = System.currentTimeMillis();
            int onlinePlayers = Bukkit.getOnlinePlayers().size();
            JSONObject serverData = new JSONObject();
            serverData.put("server", ServerSelectorGUI.currentServer);
            serverData.put("tps", tps);
            serverData.put("startTime", startTime);
            serverData.put("lastHeartBeat", lastHeartBeat);
            serverData.put("onlinePlayers", onlinePlayers);
            String serverDataJson = new Gson().toJson(serverData);

            try (Jedis jedis = KKZones.pool.getResource()) {
                jedis.hset("servers", ServerSelectorGUI.currentServer, serverDataJson);
            } catch (Exception e) {
                plugin.getLogger().severe("Could not save server data to Redis: " + e.getMessage());
            }
        }, 20L, 20L);
    }


    Runnable pingServers = () -> {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players.getOpenInventory().getTitle().equals(
                    ChatColor.translateAlternateColorCodes('&', "&dServer Selector"))) {
                players.sendMessage("test");
                new ServerSelectorGUI(plugin, players).populateServerSlots();
            }
        }
    };
}
