package com.koolkidzmc.kkzones.border;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.utils.ColorAPI;
import com.koolkidzmc.kkzones.utils.SoundAPI;
import com.koolkidzmc.kkzones.utils.TaskManager;
import com.koolkidzmc.kkzones.gui.ServerSelectorGUI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

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
                try {
                    for (Map.Entry<String, String> entry : ServerSelectorGUI.servers.entrySet()) {
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        if (server.get("server").toString().equalsIgnoreCase(plugin.getConfig().getString("servers.east"))) {
                            String serverName = server.get("server").toString();

                            if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {sendToOfflineServer(serverName, player);}
                            else if (serverName.equalsIgnoreCase(ServerSelectorGUI.currentServer)) {sendToCurrentServer(serverName, player);}
                            else {sendToOnlineServer(serverName, player);}
                        }
                    }
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("Error: " + e);
                }
            }
            if (player.getLocation().getBlockX() <= -9997.00) {
                try {
                    for (Map.Entry<String, String> entry : ServerSelectorGUI.servers.entrySet()) {
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        if (server.get("server").toString().equalsIgnoreCase(plugin.getConfig().getString("servers.west"))) {
                            String serverName = server.get("server").toString();

                            if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {sendToOfflineServer(serverName, player);}
                            else if (serverName.equalsIgnoreCase(ServerSelectorGUI.currentServer)) {sendToCurrentServer(serverName, player);}
                            else {sendToOnlineServer(serverName, player);}
                        }
                    }
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("Error: " + e);
                }
            }
            if (player.getLocation().getBlockZ() >= 9997.00) {
                try {
                    for (Map.Entry<String, String> entry : ServerSelectorGUI.servers.entrySet()) {
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        if (server.get("server").toString().equalsIgnoreCase(plugin.getConfig().getString("servers.south"))) {
                            String serverName = server.get("server").toString();

                            if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {sendToOfflineServer(serverName, player);}
                            else if (serverName.equalsIgnoreCase(ServerSelectorGUI.currentServer)) {sendToCurrentServer(serverName, player);}
                            else {sendToOnlineServer(serverName, player);}
                        }
                    }
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("Error: " + e);
                }
            }
            if (player.getLocation().getBlockZ() <= -9997.00) {
                try {
                    for (Map.Entry<String, String> entry : ServerSelectorGUI.servers.entrySet()) {
                        JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                        if (server.get("server").toString().equalsIgnoreCase(plugin.getConfig().getString("servers.north"))) {
                            String serverName = server.get("server").toString();

                            if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {sendToOfflineServer(serverName, player);}
                            else if (serverName.equalsIgnoreCase(ServerSelectorGUI.currentServer)) {sendToCurrentServer(serverName, player);}
                            else {sendToOnlineServer(serverName, player);}
                        }
                    }
                } catch (ParseException e) {
                    Bukkit.getLogger().severe("Error: " + e);
                }
            }
        }
    };

    private static void sendToOnlineServer(String serverName, Player player) {
        SoundAPI.success(player);
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(serverName);
        } catch (IOException ex) {
            Bukkit.getLogger().severe("AAHHH");
        }
        player.sendPluginMessage(KKZones.getPlugin(KKZones.class), "BungeeCord", b.toByteArray());
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
