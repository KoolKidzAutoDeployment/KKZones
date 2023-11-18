package com.koolkidzmc.kkzones.border;

import com.koolkidzmc.kkzones.KKZones;

import com.koolkidzmc.kkzones.utils.Locations;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
                Location loc = new Location(e.getPlayer().getWorld(), 230, 100, -140);
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
            Bukkit.getLogger().severe("AHHHHHHHHHHHHHHHHHHHHHHHHHH: " + ex);
        }
    }

}
