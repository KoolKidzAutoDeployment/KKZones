package com.koolkidzmc.kkzones.dataMisc;

import com.koolkidzmc.kkzones.KKZones;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.Map;

public class ServerStorage {
    private final FileConfiguration cfg;
    private final KKZones plugin;
    public static String currentServer;
    public static Map<String, String> servers;


    public ServerStorage(KKZones plugin, Map<String, String> servers) {
        this.plugin = plugin;
        this.cfg = plugin.getConfig();
        ServerStorage.servers = servers;
    }
}
