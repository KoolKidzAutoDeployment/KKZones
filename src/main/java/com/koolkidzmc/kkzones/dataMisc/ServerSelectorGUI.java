package com.koolkidzmc.kkzones.dataMisc;

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
    public static Map<String, String> servers;


    public ServerSelectorGUI(KKZones plugin, Player player, Map<String, String> servers) {
        super(27, ColorAPI.formatString("&dServer Selector"));
        this.plugin = plugin;
        this.cfg = plugin.getConfig();
        ServerSelectorGUI.servers = servers;
    }


}
