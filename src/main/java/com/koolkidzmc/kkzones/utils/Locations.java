package com.koolkidzmc.kkzones.utils;

import com.google.gson.Gson;
import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.dataMisc.ServerSelectorGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

public class Locations {

    KKZones plugin = KKZones.getPlugin(KKZones.class);
    static Gson gson = new Gson();

    public void teleport(@NotNull Player player, @NotNull String server, Location location) {
        // If current instance, just tp to location directly
        if (ServerSelectorGUI.currentServer.equalsIgnoreCase(server)) {
            player.teleport(location);
            return;
        }

        // Adding location to redis
        try {
            Jedis jedis = KKZones.pool.getResource();
            jedis.auth(plugin.getConfig().getString("redis.password"));
            jedis.set(getTeleportationToLocationKey(player), locationToString(location));
            jedis.close();
        } catch (Exception e) {
            plugin.getLogger().severe("Could not store teleportation data in redis " + e.getMessage());
        }

        new Messenger().connect(player, server);
    }

    public String getTeleportationToLocationKey(Player player) {
        return "teleportation:location:" + player.getName();
    }

    public static String locationToString(Location location) {
        // Create an intermediary object to hold the location's data
        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        // Create a simple JSON string from the intermediary object
        return gson.toJson(new String[]{worldName, String.valueOf(x), String.valueOf(y), String.valueOf(z), String.valueOf(yaw), String.valueOf(pitch)});
    }

    // Deserializes the JSON string back to a Location object
    public static Location fromLocationString(String locationString) {
        // Parse the JSON string back into an array of strings
        String[] parts = gson.fromJson(locationString, String[].class);

        // Extract the data from the parsed array
        String worldName = parts[0];
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);

        // Get the world by name
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("World with name " + worldName + " not found");
        }

        // Create a new Location object with the extracted data
        return new Location(world, x, y, z, yaw, pitch);
    }

    public String getPlayerToLocation(Player player) {
        Jedis jedis = null;
        String toLocation = "";
        try {
            jedis = KKZones.pool.getResource();
            jedis.auth(plugin.getConfig().getString("redis.password"));
            toLocation = jedis.get(getTeleportationToLocationKey(player));
            jedis.close();
        } catch (Exception e) {
            plugin.getLogger().severe("Could not retrieve teleportation data from Redis: " + e.getMessage());
        }
        return toLocation;
    }

    public void clearTeleportKeyFromRedis(String key) {
        Jedis jedis = null;
        try {
            jedis = KKZones.pool.getResource();
            jedis.auth(plugin.getConfig().getString("redis.password"));
            jedis.del(key);
            jedis.close();
        } catch (Exception e) {
            plugin.getLogger().severe("Could not delete teleportation data from Redis: " + e.getMessage());
        }
    }
}
