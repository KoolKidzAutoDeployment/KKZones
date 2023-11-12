package com.koolkidzmc.kkzones.gui.serverinfo;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.gui.ServerSelectorGUI;
import com.koolkidzmc.kkzones.utils.TaskManager;

import java.io.IOException;

public class ServerPinger {
    public void init() {
        try {
            TaskManager.Async.runTask(pingServers, 5);
        } catch (Exception e) {
            KKZones.getPlugin(KKZones.class).getLogger().warning("Error while starting Asynchronous Task [pingServers]: " + e);
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
    };

}
