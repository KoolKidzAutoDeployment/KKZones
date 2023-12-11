package com.koolkidzmc.kkzones;


import com.koolkidzmc.kkzones.utils.ColorAPI;
import com.koolkidzmc.kkzones.utils.TaskManager;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

public class PubSub extends JedisPubSub {
    @Override
    public void onMessage(String channel, String message) {
        if (!channel.equals("kkzones.chatsync")) return;
        KKZones.getPlugin(KKZones.class).console.info(ColorAPI.formatString("[StaffChat] " + message));
        TaskManager.Async.run(()->{
            for (Player player : KKZones.getPlugin(KKZones.class).getServer().getOnlinePlayers()) {
                player.sendMessage(ColorAPI.formatString(message));
            }
        });
    }
    @Override
    public void onSubscribe(String channel, int subscribedTo) {
        KKZones.getPlugin(KKZones.class).console.info("Connected to pubsub channel: " + channel);
    }
}