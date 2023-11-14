package com.koolkidzmc.kkzones.commands;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.gui.ServerSelectorGUI;
import com.koolkidzmc.kkzones.utils.SoundAPI;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import redis.clients.jedis.Jedis;

import java.util.Map;


public class ServerCommand implements CommandExecutor {
    KKZones plugin;
    public ServerCommand(KKZones plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            Jedis jedis = null;
            try {
                jedis = KKZones.pool.getResource();
                jedis.auth(plugin.getConfig().getString("redis.password"));
                Map<String, String> servers = jedis.hgetAll("zones-servers");
                SoundAPI.click(player);
                new ServerSelectorGUI(plugin, player, servers).open(player);
                jedis.close();
            } catch (Exception e) {
                player.sendMessage("errorr: " + e);
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

}
