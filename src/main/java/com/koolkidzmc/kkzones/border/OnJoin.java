package com.koolkidzmc.kkzones.border;

import com.koolkidzmc.kkzones.KKZones;

import com.koolkidzmc.kkzones.utils.Locations;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoin implements Listener {
    private final KKZones plugin;
    public OnJoin(KKZones plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        try {
            if (plugin.getConfig().getBoolean("spawn")) {
                World warpWorld = plugin.getServer().getWorld(NamespacedKey.fromString(plugin.getConfig().getString("monthly-crate-warp.world")));
                double warpX = plugin.getConfig().getDouble("x");
                double warpY = plugin.getConfig().getDouble("y");
                double warpZ = plugin.getConfig().getDouble("z");
                Location loc = new Location(warpWorld, warpX, warpY, warpZ);
                loc.setYaw((float) plugin.getConfig().getDouble("yaw"));
                e.getPlayer().teleport(loc);
                new Locations().clearTeleportKeyFromRedis(new Locations().getTeleportationToLocationKey(e.getPlayer()));
            }
            if(new Locations().getPlayerToLocation(e.getPlayer()) == null) return;
            String locStr = new Locations().getPlayerToLocation(e.getPlayer());
            Location loc = Locations.fromLocationString(locStr);

            loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 2);
            e.getPlayer().teleport(loc);
            new Locations().clearTeleportKeyFromRedis(new Locations().getTeleportationToLocationKey(e.getPlayer()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
