package com.koolkidzmc.kkzones.commands;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.gui.ServerSelectorGUI;
import com.koolkidzmc.kkzones.utils.SoundAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
            SoundAPI.success(player);
            Jedis jedis = null;
            try {
                jedis = KKZones.pool.getResource();
                jedis.auth(plugin.getConfig().getString("redis.password"));
                Map<String, String> servers = jedis.hgetAll("zones-servers");
                Integer slot = 10;
                for (Map.Entry<String, String> entry : servers.entrySet()) {
                    JSONObject server = (JSONObject) new JSONParser().parse(entry.getValue());
                    String serverName = server.get("server").toString();
                    Integer onlinePlayers = Integer.parseInt(server.get("onlinePlayers").toString());
                    Double tps = Double.parseDouble(server.get("tps").toString());

                    long miliOnline = Long.parseLong(server.get("lastHeartBeat").toString()) - Long.parseLong(server.get("startTime").toString());
                    long seconds = miliOnline / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;
                    hours %= 24;
                    minutes %= 60;
                    seconds %= 60;
                    String onlineTime = days + "&7d &f" + hours + "&7h &f" + minutes + "&7m &f" + seconds + "&7s &f";
                    new ServerSelectorGUI(plugin, player, slot, serverName, onlinePlayers, tps, onlineTime).open(player);
                    slot++;
                }
                jedis.close();
                    /*
                    int playerCount = Integer.parseInt(jedis.hget("servers", ServerSelectorGUI.currentServer));
                    int maxPlayers = Integer.parseInt(jedis.hget("server_statistics", "max_players"));

                    players.sendMessage("Player Count: " + playerCount);
                    players.sendMessage("Max Players: " + maxPlayers);

                     */
            } catch (Exception e) {
                player.sendMessage("errorr: " + e);
            }
            return true;
        }
        return false;
    }

}
