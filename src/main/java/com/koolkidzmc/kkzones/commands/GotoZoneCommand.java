package com.koolkidzmc.kkzones.commands;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.dataMisc.ServerStorage;
import com.koolkidzmc.kkzones.utils.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GotoZoneCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length >= 1) {
                try {
                    if (ServerStorage.servers.get(args[0]) == null) {
                        player.sendMessage(KKZones.getPlugin(KKZones.class).getConfig().getString("prefix") + "&cServer &f" + args[0] + "&cdoes not exist!");
                        return true;
                    }
                    JSONObject server = (JSONObject) new JSONParser().parse(ServerStorage.servers.get(args[0]));
                    if (Long.parseLong(server.get("lastHeartBeat").toString()) < System.currentTimeMillis() - 4000) {
                        player.sendMessage(KKZones.getPlugin(KKZones.class).getConfig().getString("prefix") + "&cServer &f" + args[0] + "&cis offline!");
                        return true;
                    }
                    new Messenger().connect(player, args[0]);
                    player.sendMessage(KKZones.getPlugin(KKZones.class).getConfig().getString("prefix") + "&aSending you to &f" + args[0]);
                    return true;
                } catch (ParseException e) {
                    e.printStackTrace();
                    player.sendMessage("Error lol check console.");
                    return false;
                }
            }
        }
        return false;
    }
}
