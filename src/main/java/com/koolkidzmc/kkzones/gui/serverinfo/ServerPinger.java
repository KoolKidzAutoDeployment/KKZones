package com.koolkidzmc.kkzones.gui.serverinfo;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.gui.ServerSelectorGUI;
import com.koolkidzmc.kkzones.utils.ColorAPI;
import com.koolkidzmc.kkzones.utils.ItemBuilder;
import com.koolkidzmc.kkzones.utils.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;

public class ServerPinger<ArrayList> {
    private KKZones plugin;
    public void init(KKZones plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Setting Messsage Channels...");
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        try {
            TaskManager.Async.runTask(pingServers, 5);
        } catch (Exception e) {
            plugin.getLogger().warning("Error while starting Asynchronous Task [checkLoc]: " + e);
        }
    }
    Runnable pingServers = () -> {
        for (ServerInfo servers : ServerSelectorGUI.servers.values()) {
            ServerPing ping = servers.getServerPing();
            ServerPing.DefaultResponse response;
            try {
                response = ping.fetchData();
                servers.setOnline(true);
                servers.setMotd(response.description);
                servers.setPlayerCount(response.getPlayers());
                servers.setMaxPlayers(response.getMaxPlayers());
            } catch (IOException ex) {
                servers.setOnline(false);
            }
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players.getOpenInventory().getTitle().equals(
                    ChatColor.translateAlternateColorCodes('&', "&dServer Selector"))) {
                players.getOpenInventory().getTopInventory().clear();
                for (ServerInfo server : ServerSelectorGUI.servers.values()) {

                    if (server.isOnline()) {
                        // CURRENT >
                        if (server.getServerName().equals(ServerSelectorGUI.currentServer)) {
                            String displayName = plugin.getConfig().getString("layouts.current.displayname")
                                    .replace("%server%", server.getDisplayName());

                            java.util.ArrayList<String> lore = new java.util.ArrayList<>();
                            for (String string : plugin.getConfig().getStringList("layouts.current.lore")) {
                                lore.add(ChatColor.translateAlternateColorCodes('&',
                                        string.replace("%players%", String.valueOf(server.getPlayerCount()))
                                                .replace("%max_players%", String.valueOf(server.getMaxPlayers()))
                                                .replace("%motd%", server.getMotd())));
                            }
                            ItemStack current = new ItemBuilder(Material.getMaterial(plugin.getConfig().getString("layouts.current.material"))).name(ColorAPI.formatString(displayName)).lore(lore).build();
                            if (plugin.getConfig().getBoolean("layouts.current.glow")) {
                                current.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                                current.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            }
                            players.getOpenInventory().getTopInventory().setItem(server.getSlot(), current);
                            // < CURRENT
                        } else {
                            // ONLINE >
                            String displayName = plugin.getConfig().getString("layouts.online.displayname")
                                    .replace("%server%", server.getDisplayName());

                            java.util.ArrayList<String> lore = new java.util.ArrayList<>();
                            for (String string : plugin.getConfig().getStringList("layouts.online.lore")) {
                                lore.add(ChatColor.translateAlternateColorCodes('&',
                                        string.replace("%players%", String.valueOf(server.getPlayerCount()))
                                                .replace("%max_players%", String.valueOf(server.getMaxPlayers()))
                                                .replace("%motd%", server.getMotd())));
                            }
                            ItemStack online = new ItemBuilder(Material.getMaterial(plugin.getConfig().getString("layouts.online.material"))).name(ColorAPI.formatString(displayName)).lore(lore).build();
                            if (plugin.getConfig().getBoolean("layouts.online.glow")) {
                                online.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                                online.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            }
                            players.getOpenInventory().getTopInventory().setItem(server.getSlot(), online);
                            // < ONLINE
                        }
                    } else {
                        // OFFLINE >
                        String displayName = plugin.getConfig().getString("layouts.offline.displayname")
                                .replace("%server%", server.getDisplayName());

                        java.util.ArrayList<String> lore = new java.util.ArrayList<>();
                        for (String string : plugin.getConfig().getStringList("layouts.offline.lore")) {
                            lore.add(ChatColor.translateAlternateColorCodes('&',
                                    string.replace("%players%", "0")
                                            .replace("%max_players%", "??")
                                            .replace("%motd%", "????")));
                        }
                        ItemStack offline = new ItemBuilder(Material.getMaterial(plugin.getConfig().getString("layouts.offline.material"))).name(ColorAPI.formatString(displayName)).lore(lore).build();
                        if (plugin.getConfig().getBoolean("layouts.offline.glow")) {
                            offline.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                            offline.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        }
                        players.getOpenInventory().getTopInventory().setItem(server.getSlot(), offline);
                        // < OFFLINE
                    }

                }
            }
        }
    };
}
