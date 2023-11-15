package com.koolkidzmc.kkzones.dataMisc;

import com.koolkidzmc.kkzones.KKZones;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.Map;

public class ServerSelectorGUI {
    private final FileConfiguration cfg;
    private final KKZones plugin;
    public static String currentServer;
    public static Map<String, String> servers;


    public ServerSelectorGUI(KKZones plugin, Map<String, String> servers) {
        this.plugin = plugin;
        this.cfg = plugin.getConfig();
        ServerSelectorGUI.servers = servers;
    }


}
