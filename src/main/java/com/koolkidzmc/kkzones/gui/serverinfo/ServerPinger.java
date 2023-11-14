package com.koolkidzmc.kkzones.gui.serverinfo;

import com.google.gson.Gson;
import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.gui.ServerSelectorGUI;
import com.koolkidzmc.kkzones.utils.ColorAPI;
import com.koolkidzmc.kkzones.utils.ItemBuilder;
import com.koolkidzmc.kkzones.utils.SoundAPI;
import com.koolkidzmc.kkzones.utils.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
            TaskManager.Async.runTask(pingServers, 20L);
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
            if (players.getOpenInventory().getTitle().equals(
                    ChatColor.translateAlternateColorCodes('&', "&dServer Selector"))) {
                Jedis jedis = null;
                try {
                    jedis = KKZones.pool.getResource();
                    jedis.auth(plugin.getConfig().getString("redis.password"));
                    Map<String, String> servers = jedis.hgetAll("zones-servers");
                    Integer slot = 10;
                    for (Map.Entry<String, String> entry : servers.entrySet()) {
                        if (slot > 16) return;
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        String serverName = server.get("server").toString();
                        Integer onlinePlayers = Integer.parseInt(server.get("onlinePlayers").toString());
                        Double tps = Double.parseDouble(server.get("tps").toString());

                        long miliOnline = Long.parseLong(server.get("lastHeartBeat").toString()) - Long.parseLong(server.get("startTime").toString());
                        long seconds = miliOnline / 1000;
                        long minutes = seconds / 60;
                        long hours = minutes / 60;
                        long days = hours / 24;
                        hours %= 24;
                        minutes %= 60;
                        seconds %= 60;
                        String onlineTime = days + "&7d &f" + hours + "&7h &f" + minutes + "&7m &f" + seconds + "&7s &f";
                        populateServerSlots(slot, serverName, onlinePlayers, tps, onlineTime, players);
                        slot++;
                    }
                    jedis.close();
                    /*
                    int playerCount = Integer.parseInt(jedis.hget("servers", ServerSelectorGUI.currentServer));
                    int maxPlayers = Integer.parseInt(jedis.hget("server_statistics", "max_players"));

                    players.sendMessage("Player Count: " + playerCount);
                    players.sendMessage("Max Players: " + maxPlayers);

                     */
                } catch (Exception e) {
                    players.sendMessage("errorr: " + e);
                }
            }
        }
    };
    public void populateServerSlots(Integer slot, String serverName, Integer onlinePlayers, Double tps, String onlineTime, Player player) {
        double tpsFixed = Math.round(tps * 100.0) / 100.0;
        new ServerSelectorGUI(plugin, player).setItem(slot, new ItemBuilder(Material.EMERALD_BLOCK)
                .name(ColorAPI.formatString("&a" + serverName))
                .addLore(ColorAPI.formatString("&fClick to join &a" + onlinePlayers + " &fother players!"))
                .addLore(" ")
                .addLore(ColorAPI.formatString("&8Server Info"))
                .addLore(ColorAPI.formatString("&f&l| &fTPS: &a" + tpsFixed))
                .addLore(ColorAPI.formatString("&f&l| &fOnline For: &f" + onlineTime))
                .build(), e -> {
            Player players = (Player) e.getWhoClicked();
            SoundAPI.success(players);
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Connect");
                out.writeUTF(serverName);
            } catch (IOException ex) {
                Bukkit.getLogger().severe("AAHHH");
            }
            players.sendPluginMessage(KKZones.getPlugin(KKZones.class), "BungeeCord", b.toByteArray());
        });
    }
}
