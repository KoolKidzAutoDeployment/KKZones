package com.koolkidzmc.kkzones;

import com.koolkidzmc.kkzones.border.BorderChecker;
import com.koolkidzmc.kkzones.border.OnJoin;
import com.koolkidzmc.kkzones.commands.GotoZoneCommand;
import com.koolkidzmc.kkzones.commands.ZonesCommand;
import com.koolkidzmc.kkzones.dataMisc.ServerStorage;
import com.koolkidzmc.kkzones.dataMisc.ServerPinger;
import com.koolkidzmc.kkzones.utils.FastInvManager;

import com.koolkidzmc.kkzones.utils.TaskManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisException;

import java.util.logging.Logger;

public final class KKZones extends JavaPlugin {
    Logger console = getLogger();
    FileConfiguration config = getConfig();
    public static JedisPool pool;
    public Jedis jedis = null;
    JedisPubSub pubSub;
    @Override
    public void onEnable() {
        console.info("Starting KKZones on the " + config.getString("server") + " server!");

        initConfig();

        ServerStorage.currentServer = config.getString("server");
        FastInvManager.register(this);
        pubSub = new PubSub();

        this.getCommand("zones").setExecutor(new ZonesCommand(this));
        this.getCommand("zone").setExecutor(new GotoZoneCommand());

        startListeners();
    }

    @Override
    public void onDisable() {
        pool.close();
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

    private void startListeners() {
        console.info("Starting Listeners...");
        console.info("Starting Join Listener...");
        this.getServer().getPluginManager().registerEvents(new OnJoin(this), this);
        console.info("Setting Outgoing Plugin Channel to [BungeeCord]...");
        try {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            console.info("Outgoing Plugin Channel Set!");
        } catch (Exception e) {
            console.severe("Error Setting Plugin Channel: " + e);
        }

        console.info("Starting & Connecting to Redis Pool...");
        try {
            pool = new JedisPool(config.getString("redis.host"), config.getInt("redis.port"));
            pool.setMaxTotal(35);
            initRedisPubSub();
            console.info("Redis Connected Successfully!");
        } catch (JedisException e)  {
            console.severe("Error Starting Redis Pool: " + e);
        }
        console.info("Starting Asynchronous Tasks...");
        try {
            new BorderChecker().init(this);
            new ServerPinger().init(this);
        } catch (Exception e) {
            console.severe("Error Starting Asynchronous Tasks: " + e);
        }
        console.info("Listeners Started!");
    }

    private void initRedisPubSub(){
        console.info("Starting & Connecting to Redis PubSub...");
        try {
            jedis = pool.getResource();
            console.info("Attempting password: " + config.getString("redis.password"));
            jedis.auth(config.getString("redis.password"));
            TaskManager.Async.run(()-> {
                jedis.subscribe(pubSub, "kkzones.chatsync");
            });
            console.info("Redis Connected pubsub!");
        } catch (JedisException e)  {
            console.severe("Error Starting Redis pubsub: " + e);
            e.printStackTrace();
        }
    }

}
