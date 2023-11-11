package com.koolkidzmc.kkzones.messaging;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.koolkidzmc.kkzones.KKZones;
import org.bukkit.entity.Player;

public class MessageSender {

    private final KKZones plugin;
    public MessageSender(KKZones plugin) {
        this.plugin = plugin;
    }
    public void transferServer(String server, Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

}
