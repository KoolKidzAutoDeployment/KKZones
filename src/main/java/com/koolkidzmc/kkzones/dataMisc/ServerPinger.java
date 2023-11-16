package com.koolkidzmc.kkzones.dataMisc;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.utils.TaskManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;

import java.util.Map;

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
            long lastHeartBeat = System.currentTimeMillis();
            JSONObject serverData = new JSONObject();
            serverData.put("server", ServerStorage.currentServer);
            serverData.put("lastHeartBeat", lastHeartBeat);
            String serverDataJson = new Gson().toJson(serverData);

            Jedis jedis = null;
            try {
                jedis = KKZones.pool.getResource();
                jedis.auth(plugin.getConfig().getString("redis.password"));
                jedis.hset("zones-heartbeat", ServerStorage.currentServer, serverDataJson);
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
                Map<String, String> servers = jedis.hgetAll("zones-heartbeat");
                new ServerStorage(plugin, servers);
                jedis.close();
            } catch (Exception e) {
                players.sendMessage("errorr: " + e);
                e.printStackTrace();
            }
        }
    };
}
