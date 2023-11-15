package com.koolkidzmc.kkzones.border;

import com.koolkidzmc.kkzones.KKZones;

import com.koolkidzmc.kkzones.utils.Locations;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
            String locStr = new Locations().getPlayerToLocation(e.getPlayer());
            Location loc = Locations.fromLocationString(locStr);

            World world = e.getPlayer().getWorld();
            int highY = world.getHighestBlockYAt(loc);
            loc.setY(highY);
            e.getPlayer().teleport(loc);
        } catch (Exception ex) {
            Bukkit.getLogger().severe("AHHHHHHHHHHHHHHHHHHHHHHHHHH: " + ex);
        }
    }

}
