package com.koolkidzmc.kkzones.border;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.utils.ColorAPI;
import com.koolkidzmc.kkzones.utils.Locations;
import com.koolkidzmc.kkzones.utils.SoundAPI;
import com.koolkidzmc.kkzones.utils.TaskManager;
import com.koolkidzmc.kkzones.dataMisc.ServerStorage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

public class BorderChecker {
    static KKZones plugin;
    public void init(KKZones plugin) {
        BorderChecker.plugin = plugin;
        plugin.getLogger().info("Setting Message Channels...");
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        if (plugin.getConfig().getBoolean("spawn")) {
            try {
                TaskManager.Async.runTask(checkLocSpawn, 5);
            } catch (Exception e) {
                plugin.getLogger().warning("Error while starting Asynchronous Task [checkLocSpawn]: " + e);
            }
        } else {
            try {
                TaskManager.Async.runTask(checkLocZones, 5);
            } catch (Exception e) {
                plugin.getLogger().warning("Error while starting Asynchronous Task [checkLocZones]: " + e);
            }
        }
    }

    static Runnable checkLocSpawn = () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
            double size = (player.getWorld().getWorldBorder().getSize()/2)-3;
            double centerX = player.getWorld().getWorldBorder().getCenter().getX();
            double centerZ = player.getWorld().getWorldBorder().getCenter().getZ();
            double north = centerZ - size;
            double south = centerZ + size;
            double east = centerX + size;
            double west = centerX - size;
            if (player.getLocation().getBlockX() >= east) {
                try {
                    for (Map.Entry<String, String> entry : ServerStorage.servers.entrySet()) {
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        if (server.get("server").toString().equalsIgnoreCase(plugin.getConfig().getString("servers.east"))) {
                            String serverName = server.get("server").toString();

                            if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {sendToOfflineServer(serverName, player);}
                            else if (serverName.equalsIgnoreCase(ServerStorage.currentServer)) {sendToCurrentServer(serverName, player);}
                            else {sendToOnlineServer(serverName, player, "east");}
                        }
                    }
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("Error: " + e);
                }
            }
            if (player.getLocation().getBlockX() <= west) {
                try {
                    for (Map.Entry<String, String> entry : ServerStorage.servers.entrySet()) {
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        if (server.get("server").toString().equalsIgnoreCase(plugin.getConfig().getString("servers.west"))) {
                            String serverName = server.get("server").toString();

                            if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {sendToOfflineServer(serverName, player);}
                            else if (serverName.equalsIgnoreCase(ServerStorage.currentServer)) {sendToCurrentServer(serverName, player);}
                            else {sendToOnlineServer(serverName, player, "west");}
                        }
                    }
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("Error: " + e);
                }
            }
            if (player.getLocation().getBlockZ() >= south) {
                try {
                    for (Map.Entry<String, String> entry : ServerStorage.servers.entrySet()) {
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        if (server.get("server").toString().equalsIgnoreCase(plugin.getConfig().getString("servers.south"))) {
                            String serverName = server.get("server").toString();

                            if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {sendToOfflineServer(serverName, player);}
                            else if (serverName.equalsIgnoreCase(ServerStorage.currentServer)) {sendToCurrentServer(serverName, player);}
                            else {sendToOnlineServer(serverName, player, "south");}
                        }
                    }
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("Error: " + e);
                }
            }
            if (player.getLocation().getBlockZ() <= north) {
                try {
                    for (Map.Entry<String, String> entry : ServerStorage.servers.entrySet()) {
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        if (server.get("server").toString().equalsIgnoreCase(plugin.getConfig().getString("servers.north"))) {
                            String serverName = server.get("server").toString();

                            if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {sendToOfflineServer(serverName, player);}
                            else if (serverName.equalsIgnoreCase(ServerStorage.currentServer)) {sendToCurrentServer(serverName, player);}
                            else {sendToOnlineServer(serverName, player, "north");}
                        }
                    }
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("Error: " + e);
                }
            }
        }
    };


    static Runnable checkLocZones = () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().getBlockX() >= 9997.00) {
                try {
                    for (Map.Entry<String, String> entry : ServerStorage.servers.entrySet()) {
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        if (server.get("server").toString().equalsIgnoreCase(plugin.getConfig().getString("servers.east"))) {
                            String serverName = server.get("server").toString();

                            if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {sendToOfflineServer(serverName, player);}
                            else if (serverName.equalsIgnoreCase(ServerStorage.currentServer)) {sendToCurrentServer(serverName, player);}
                            else {sendToOnlineServer(serverName, player, "east");}
                        }
                    }
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("Error: " + e);
                }
            }
            if (player.getLocation().getBlockX() <= -9997.00) {
                try {
                    for (Map.Entry<String, String> entry : ServerStorage.servers.entrySet()) {
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        if (server.get("server").toString().equalsIgnoreCase(plugin.getConfig().getString("servers.west"))) {
                            String serverName = server.get("server").toString();

                            if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {sendToOfflineServer(serverName, player);}
                            else if (serverName.equalsIgnoreCase(ServerStorage.currentServer)) {sendToCurrentServer(serverName, player);}
                            else {sendToOnlineServer(serverName, player, "west");}
                        }
                    }
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("Error: " + e);
                }
            }
            if (player.getLocation().getBlockZ() >= 9997.00) {
                try {
                    for (Map.Entry<String, String> entry : ServerStorage.servers.entrySet()) {
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        if (server.get("server").toString().equalsIgnoreCase(plugin.getConfig().getString("servers.south"))) {
                            String serverName = server.get("server").toString();

                            if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {sendToOfflineServer(serverName, player);}
                            else if (serverName.equalsIgnoreCase(ServerStorage.currentServer)) {sendToCurrentServer(serverName, player);}
                            else {sendToOnlineServer(serverName, player, "south");}
                        }
                    }
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("Error: " + e);
                }
            }
            if (player.getLocation().getBlockZ() <= -9997.00) {
                try {
                    for (Map.Entry<String, String> entry : ServerStorage.servers.entrySet()) {
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        if (server.get("server").toString().equalsIgnoreCase(plugin.getConfig().getString("servers.north"))) {
                            String serverName = server.get("server").toString();

                            if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {sendToOfflineServer(serverName, player);}
                            else if (serverName.equalsIgnoreCase(ServerStorage.currentServer)) {sendToCurrentServer(serverName, player);}
                            else {sendToOnlineServer(serverName, player, "north");}
                        }
                    }
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("Error: " + e);
                }
            }
        }
    };

    private static void sendToOnlineServer(String serverName, Player player, String border) {
        SoundAPI.success(player);
        player.setVelocity(player.getLocation().getDirection().multiply(-1));
        Location loc = player.getLocation();
        player.sendMessage("Sending To Server: " + serverName);

        if (border.equalsIgnoreCase("east")) loc.setX(-9995.00);
        if (border.equalsIgnoreCase("west")) loc.setX(9995.00);
        if (border.equalsIgnoreCase("north")) loc.setZ(9995.00);
        if (border.equalsIgnoreCase("south")) loc.setZ(-9995.00);

        new Locations().teleport(player, serverName, loc);
    }

    private static void sendToCurrentServer(String serverName, Player player) {
        SoundAPI.fail(player);
        player.setVelocity(player.getLocation().getDirection().multiply(-1));
        player.sendMessage(ColorAPI.formatString("&cError connecting to server: You are already connected to " + serverName + "!"));
    }
    private static void sendToOfflineServer(String serverName, Player player) {
        SoundAPI.fail(player);
        player.setVelocity(player.getLocation().getDirection().multiply(-1));
        player.sendMessage(ColorAPI.formatString("&cError connecting to server: " + serverName + " &cis offline!"));
    }
}
