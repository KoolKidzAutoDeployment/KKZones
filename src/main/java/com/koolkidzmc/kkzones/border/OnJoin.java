package com.koolkidzmc.kkzones.border;


import com.google.gson.JsonParser;
import com.koolkidzmc.kkzones.KKZones;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import redis.clients.jedis.Jedis;


public class OnJoin implements Listener {
    private final KKZones plugin;
    public OnJoin(KKZones plugin) {
        this.plugin = plugin;
    }
    public void onPlayerJoin(PlayerJoinEvent e) {
        Jedis jedis = null;
        try {
            jedis = KKZones.pool.getResource();
            jedis.auth(plugin.getConfig().getString("redis.password"));
            JSONObject transferPacket = (JSONObject) new JSONParser().parse(jedis.hget("zones-transfers", e.getPlayer().getUniqueId().toString()));
            e.getPlayer().sendMessage(transferPacket.toString());
            jedis.close();
        } catch (Exception ex) {
            e.getPlayer().sendMessage("errorr: " + ex);
            ex.printStackTrace();
        }
    }
}
