package com.koolkidzmc.kkzones;

import com.koolkidzmc.kkzones.border.BorderChecker;
import com.koolkidzmc.kkzones.commands.ServerCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class KKZones extends JavaPlugin {
    Logger console = getLogger();
    FileConfiguration config = getConfig();
    @Override
    public void onEnable() {
        console.info("Starting KKZones on the " + config.getString("server") + " server!");
        initConfig();
        BorderChecker.init(this);
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

    private void registerCommands() {
        this.getCommand("server").setExecutor(new ServerCommand(this));

    }
}
