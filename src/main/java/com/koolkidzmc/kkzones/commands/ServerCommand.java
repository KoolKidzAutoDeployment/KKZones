package com.koolkidzmc.kkzones.commands;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.gui.ServerSelectorGUI;
import com.koolkidzmc.kkzones.gui.serverinfo.ServerPing;
import com.koolkidzmc.kkzones.utils.SoundAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ServerCommand implements CommandExecutor {
    KKZones plugin;
    public ServerCommand(KKZones plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            SoundAPI.success(player);
            new ServerSelectorGUI(plugin, player).open(player);
            return true;
        }
        return false;
    }

}
