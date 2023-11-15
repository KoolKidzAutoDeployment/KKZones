package com.koolkidzmc.kkzones.utils;

import com.google.gson.Gson;
import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.gui.ServerSelectorGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
        try (Jedis jedis = KKZones.pool.getResource()) {
            jedis.set(getTeleportationToLocationKey(player), locationToString(location));
        } catch (Exception e) {
            plugin.getLogger().severe("Could not store teleportation data in redis " + e.getMessage());
        }

        new Messenger().connect(player, server);
    }

    private String getTeleportationToLocationKey(Player player) {
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

    public CompletableFuture<String> getPlayerToLocation(Player player) {
        CompletableFuture<String> future = new CompletableFuture<>();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = KKZones.pool.getResource()) {
                String toLocation = jedis.get(getTeleportationToLocationKey(player));
                future.complete(toLocation);
            } catch (Exception e) {
                plugin.getLogger().severe("Could not retrieve teleportation data from Redis: " + e.getMessage());
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public void clearTeleportKeyFromRedis(String key) {
        try (Jedis jedis = KKZones.pool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            plugin.getLogger().severe("Could not delete teleportation data from Redis: " + e.getMessage());
        }
    }
}
