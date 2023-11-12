package com.koolkidzmc.kkzones.gui.serverinfo;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.gui.ServerSelectorGUI;
import com.koolkidzmc.kkzones.utils.TaskManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class ServerPinger {
    private FileConfiguration cfg;

    public void init(FileConfiguration cfg) {
        this.cfg = cfg;

        try {
            TaskManager.Async.runTask(pingServers, 5);
        } catch (Exception e) {
            KKZones.getPlugin(KKZones.class).getLogger().warning("Error while starting Asynchronous Task [pingServers]: " + e);
        }
    }

    Runnable pingServers = () -> {
        for (String servers : cfg.getConfigurationSection("server-list").getKeys(false)) {
            String host = cfg.getString("server-list." + servers + ".host");
            int port = cfg.getInt("server-list." + servers + ".port");
            String displayName = cfg.getString("server-list." + servers + ".displayname");
            int slot = cfg.getInt("server-list." + servers + ".slot");
            ServerSelectorGUI.servers.put(servers, new ServerInfo(servers, host, port, displayName, slot));
        }

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
    };

}
