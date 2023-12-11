package com.koolkidzmc.kkzones.dataMisc;

import com.koolkidzmc.kkzones.KKZones;

import com.koolkidzmc.kkzones.utils.ColorAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;


public class MessageSyncer implements Listener {
    @EventHandler
    private void sendMessage(AsyncPlayerChatEvent e) {
        Jedis jedis = null;
        try {
            String message = ColorAPI.formatString(chatter.getPlayerPrefix(player) + player.getName() + chatter.getPlayerSuffix(player) + ": " + e.getMessage());

            // Chat Logging
            String logMsg = "[" + plugin.getConfig().getString("server") + "] " +  player.getName() + ": " + e.getMessage();
            try (WebhookClient client = WebhookClient.withUrl(plugin.getConfig().getString("webhooks.chat"))) {
                client.send(logMsg);
            }
            jedis = KKZones.pool.getResource();
            jedis.publish("kkzones.chatsync", e.getMessage());
            e.setCancelled(true);
        } catch (JedisException ex)  {
            KKZones.getPlugin(KKZones.class).getLogger().severe("Error using Redis pubsub: " + ex);
            ex.printStackTrace();
        }
    }
}
