package com.koolkidzmc.kkzones.gui;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonParseException;
import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.utils.ColorAPI;
import com.koolkidzmc.kkzones.utils.FastInv;
import com.koolkidzmc.kkzones.utils.ItemBuilder;
import com.koolkidzmc.kkzones.utils.SoundAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ServerSelectorGUI extends FastInv {
    private final FileConfiguration cfg;
    private final KKZones plugin;
    public static String currentServer;


    public ServerSelectorGUI(KKZones plugin, Player player, Map<String, String> servers) {
        super(27, ColorAPI.formatString("&dServer Selector"));
        this.plugin = plugin;
        this.cfg = plugin.getConfig();

        fillBackground();
        try {
            Integer slot = 10;
            for (Map.Entry<String, String> entry : servers.entrySet()) {
                if (slot > 16) return;
                JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                String serverName = server.get("server").toString();
                if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {
                    populateOfflineServerSlot(slot, serverName);
                } else {
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
                    populateOnlineServerSlot(slot, serverName, onlinePlayers, tps, onlineTime);
                }
                slot++;
            }
        } catch (ParseException e) {
            Bukkit.getLogger().severe("Error: " + e);
        }

        addNavigationButtons(player);
    }

    private void populateOnlineServerSlot(Integer slot, String serverName, Integer onlinePlayers, Double tps, String onlineTime) {
        double tpsFixed = Math.round(tps * 100.0) / 100.0;
        setItem(slot, new ItemBuilder(Material.EMERALD_BLOCK)
                .name(ColorAPI.formatString("&a" + serverName))
                .addLore(ColorAPI.formatString("&fClick to join &a" + onlinePlayers + " &fother players!"))
                .addLore(" ")
                .addLore(ColorAPI.formatString("&8Server Info"))
                .addLore(ColorAPI.formatString("&f&l| &fTPS: &a" + tpsFixed))
                .addLore(ColorAPI.formatString("&f&l| &fOnline For: &f" + onlineTime))
                .build(), e -> {
            Player player = (Player) e.getWhoClicked();
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
        });
    }

    private void populateCurrentServerSlot(Integer slot, String serverName, Integer onlinePlayers, Double tps, String onlineTime) {
        double tpsFixed = Math.round(tps * 100.0) / 100.0;
        setItem(slot, new ItemBuilder(Material.EMERALD)
                .name(ColorAPI.formatString("&a" + serverName))
                .addLore(ColorAPI.formatString("&fClick to join &a" + onlinePlayers + " &fother players!"))
                .addLore(" ")
                .addLore(ColorAPI.formatString("&8Server Info"))
                .addLore(ColorAPI.formatString("&f&l| &fTPS: &a" + tpsFixed))
                .addLore(ColorAPI.formatString("&f&l| &fOnline For: &f" + onlineTime))
                .enchant(Enchantment.MENDING)
                .flags(ItemFlag.HIDE_ENCHANTS)
                .build(), e -> {
            Player player = (Player) e.getWhoClicked();
            SoundAPI.fail(player);
            player.sendMessage(ColorAPI.formatString("&cError connecting to server: You are already connected to " + serverName + "!"));
        });
    }
    private void populateOfflineServerSlot(Integer slot, String serverName) {
        setItem(slot, new ItemBuilder(Material.REDSTONE_BLOCK)
                .name(ColorAPI.formatString("&c" + serverName))
                .addLore(ColorAPI.formatString("&fServer Offline!"))
                .build(), e -> {
            Player player = (Player) e.getWhoClicked();
            SoundAPI.fail(player);
            player.sendMessage(ColorAPI.formatString("&cError connecting to server: " + serverName + " &cis offline!"));
        });
    }



    private void fillBackground() {
        for (int i = 0; i < 9; i++) {
            setItem(i, createBackgroundItem().build());
            setItem(i + 18, createBackgroundItem().build());
        }
        for (int i = 1; i < 2; i++) {
            setItem(i * 9, createBackgroundItem().build());
            setItem(i * 9 + 8, createBackgroundItem().build());
        }
    }
    private ItemBuilder createBackgroundItem() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .lore(ColorAPI.formatString("&8www.koolkidzmc.com"));
    }
    private void addNavigationButtons(Player player) {
        setItem(18, new ItemBuilder(Material.BARRIER)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .name(ColorAPI.formatString("&c&lClose"))
                .addLore(ColorAPI.formatString("&7➜ Click to close"))
                .build(), e -> {
            SoundAPI.fail(player);
            Objects.requireNonNull(e.getClickedInventory()).close();
        });
    }
}
