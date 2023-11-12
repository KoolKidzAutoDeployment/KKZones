package com.koolkidzmc.kkzones;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.koolkidzmc.kkzones.border.BorderChecker;
import com.koolkidzmc.kkzones.commands.ServerCommand;
import com.koolkidzmc.kkzones.gui.ServerSelectorGUI;
import com.koolkidzmc.kkzones.gui.serverinfo.ServerPinger;
import com.koolkidzmc.kkzones.utils.FastInvManager;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.logging.Logger;

public final class KKZones extends JavaPlugin implements PluginMessageListener {
    Logger console = getLogger();
    FileConfiguration config = getConfig();
    @Override
    public void onEnable() {
        console.info("Starting KKZones on the " + config.getString("server") + " server!");
        initConfig();
        FastInvManager.register(this);
        console.info("Starting Asynchronous Tasks...");
        BorderChecker.init(this);
        new ServerPinger().init(this);
        console.info("Asynchronous Tasks Started!");
        registerCommands();
    }

    @Override
    public void onDisable() {
        console.info("Stopping KKZones on the " + config.getString("server") + " server!");
    }

    private void initConfig() {
        console.info("Loading Config File...");
        try {
            saveDefaultConfig();
            reloadConfig();
            console.info("Config Loaded!");
        } catch (Error e) {
            console.warning("Error Loading Config: " + e);
        }
    }
    public void getServer(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");

        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("GetServer")) {
            ServerSelectorGUI.currentServer = in.readUTF();
        }
    }

    private void registerCommands() {
        this.getCommand("server").setExecutor(new ServerCommand(this));

    }
}
