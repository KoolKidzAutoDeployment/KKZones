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

            Jedis jedis = null;
            try {
                jedis = KKZones.pool.getResource();
                jedis.hset("servers", ServerSelectorGUI.currentServer, serverDataJson);
            } catch (Exception e) {
                plugin.getLogger().severe("Could not save server data to Redis: " + e.getMessage());
            } finally {
                assert jedis != null;
                jedis.close();
            }
        }, 20L, 20L);
    }


    Runnable pingServers = () -> {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players.getOpenInventory().getTitle().equals(
                    ChatColor.translateAlternateColorCodes('&', "&dServer Selector"))) {
                Jedis jedis = null;
                try {
                    jedis = KKZones.pool.getResource();
                    players.sendMessage(jedis.hget("servers", ServerSelectorGUI.currentServer));
                    /*
                    int playerCount = Integer.parseInt(jedis.hget("servers", ServerSelectorGUI.currentServer));
                    int maxPlayers = Integer.parseInt(jedis.hget("server_statistics", "max_players"));

                    players.sendMessage("Player Count: " + playerCount);
                    players.sendMessage("Max Players: " + maxPlayers);

                     */
                } catch (Exception e) {
                    players.sendMessage("errorr: " + e);
                }
                new ServerSelectorGUI(plugin, players).populateServerSlots();
            }
        }
    };
}
