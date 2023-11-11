package com.koolkidzmc.kkzones;

import com.koolkidzmc.kkzones.border.BorderChecker;
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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
}
