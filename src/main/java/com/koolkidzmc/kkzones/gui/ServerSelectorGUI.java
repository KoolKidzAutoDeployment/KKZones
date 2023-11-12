package com.koolkidzmc.kkzones.gui;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.gui.serverinfo.ServerInfo;
import com.koolkidzmc.kkzones.utils.ColorAPI;
import com.koolkidzmc.kkzones.utils.FastInv;
import com.koolkidzmc.kkzones.utils.ItemBuilder;
import com.koolkidzmc.kkzones.utils.SoundAPI;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerSelectorGUI extends FastInv {
    private boolean preventClose = true;
    private final FileConfiguration cfg;
    private KKZones plugin;
    public static HashMap<String, ServerInfo> servers = new HashMap<>();
    public static String currentServer;
    private Player player;

    public ServerSelectorGUI(KKZones plugin, Player player) {
        super(27, ColorAPI.formatString("&dServer Selector"));
        this.plugin = plugin;
        this.player = player;
        this.cfg = plugin.getConfig();

        for (String server : cfg.getConfigurationSection("server-list").getKeys(false)) {
            String host = cfg.getString("server-list." + server + ".host");
            int port = cfg.getInt("server-list." + server + ".port");
            String displayName = cfg.getString("server-list." + server + ".displayname");
            int slot = cfg.getInt("server-list." + server + ".slot");
            servers.put(server, new ServerInfo(server, host, port, displayName, slot));
        }

        fillBackground();
        populateServerSlots();
        addNavigationButtons(player);
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

    private void populateServerSlots() {
        Map<Integer, Integer> slotMap = new HashMap<>();
        for (int i = 0; i < 21; i++) {
            slotMap.put(i, i + 10);
        }

        for (ServerInfo server : servers.values()) {

            if (server.isOnline()) {
                // CURRENT >
                if (server.getServerName().equals(currentServer)) {
                    String displayName = plugin.getConfig().getString("layouts.current.displayname")
                            .replace("%server%", server.getDisplayName());

                    ArrayList<String> lore = new ArrayList<>();
                    for (String string : cfg.getStringList("layouts.current.lore")) {
                        lore.add(ChatColor.translateAlternateColorCodes('&',
                                string.replace("%players%", String.valueOf(server.getPlayerCount()))
                                        .replace("%max_players%", String.valueOf(server.getMaxPlayers()))
                                        .replace("%motd%", server.getMotd())));
                    }
                    ItemStack current = new ItemBuilder(Material.getMaterial(cfg.getString("layouts.current.material"))).name(ColorAPI.formatString(displayName)).lore(lore).build();
                    if (cfg.getBoolean("layouts.current.glow")) {
                        current.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                        current.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    setItem(server.getSlot(), current);
                    // < CURRENT
                } else {
                    // ONLINE >
                    String displayName = plugin.getConfig().getString("layouts.online.displayname")
                            .replace("%server%", server.getDisplayName());

                    ArrayList<String> lore = new ArrayList<>();
                    for (String string : cfg.getStringList("layouts.online.lore")) {
                        lore.add(ChatColor.translateAlternateColorCodes('&',
                                string.replace("%players%", String.valueOf(server.getPlayerCount()))
                                        .replace("%max_players%", String.valueOf(server.getMaxPlayers()))
                                        .replace("%motd%", server.getMotd())));
                    }
                    ItemStack online = new ItemBuilder(Material.getMaterial(cfg.getString("layouts.online.material"))).name(ColorAPI.formatString(displayName)).lore(lore).build();
                    if (cfg.getBoolean("layouts.online.glow")) {
                        online.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                        online.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    setItem(server.getSlot(), online);
                    // < ONLINE
                }
            } else {
                // OFFLINE >
                String displayName = plugin.getConfig().getString("layouts.offline.displayname")
                        .replace("%server%", server.getDisplayName());

                ArrayList<String> lore = new ArrayList<>();
                for (String string : cfg.getStringList("layouts.offline.lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&',
                            string.replace("%players%", "0")
                                    .replace("%max_players%", "??")
                                    .replace("%motd%", "????")));
                }
                ItemStack offline = new ItemBuilder(Material.getMaterial(cfg.getString("layouts.offline.material"))).name(ColorAPI.formatString(displayName)).lore(lore).build();
                if (cfg.getBoolean("layouts.offline.glow")) {
                    offline.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                    offline.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                setItem(server.getSlot(), offline);
                // < OFFLINE
            }

        }
    }

    private void addNavigationButtons(Player player) {
        setItem(18, createNavigationItem(Material.BARRIER, "&c&lClose", "&7\u279C Click to close"), e -> {
            SoundAPI.fail(player);
            e.getClickedInventory().close();
        });
    }

    private ItemStack createNavigationItem(Material material, String displayName, String lore) {
        return new ItemBuilder(material)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .name(ColorAPI.formatString(displayName))
                .addLore(ColorAPI.formatString(lore))
                .build();
    }


}
