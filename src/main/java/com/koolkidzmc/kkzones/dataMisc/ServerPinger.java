package com.koolkidzmc.kkzones.dataMisc;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.dataMisc.ServerSelectorGUI;
import com.koolkidzmc.kkzones.utils.TaskManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;

import java.util.Map;

import static com.koolkidzmc.kkzones.dataMisc.ServerSelectorGUI.servers;

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
            TaskManager.Sync.runTask(pingServers, 20L);
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
            serverData.put("slot", plugin.getConfig().getInt("servercommand-slot"));
            serverData.put("tps", tps);
            serverData.put("startTime", startTime);
            serverData.put("lastHeartBeat", lastHeartBeat);
            serverData.put("onlinePlayers", onlinePlayers);
            String serverDataJson = new Gson().toJson(serverData);

            Jedis jedis = null;
            try {
                jedis = KKZones.pool.getResource();
                jedis.auth(plugin.getConfig().getString("redis.password"));
                jedis.hset("zones-servers", ServerSelectorGUI.currentServer, serverDataJson);
                jedis.close();
            } catch (Exception e) {
                plugin.getLogger().severe("Could not save server data to Redis: " + e.getMessage());
            }
        }, 20L, 20L);
    }


    Runnable pingServers = () -> {
        for (Player players : Bukkit.getOnlinePlayers()) {
            Jedis jedis = null;
            try {
                jedis = KKZones.pool.getResource();
                jedis.auth(plugin.getConfig().getString("redis.password"));
                Map<String, String> servers = jedis.hgetAll("zones-servers");
                new ServerSelectorGUI(plugin, players, servers);
                jedis.close();
            } catch (Exception e) {
                players.sendMessage("errorr: " + e);
                e.printStackTrace();
            }
            if (players.getOpenInventory().getTitle().equals(
                    ChatColor.translateAlternateColorCodes('&', "&dServer Selector"))) {
                    new ServerSelectorGUI(plugin, players, servers).open(players);
            }
        }
    };
}
