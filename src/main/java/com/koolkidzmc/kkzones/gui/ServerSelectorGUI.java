package com.koolkidzmc.kkzones.gui;

import com.koolkidzmc.kkzones.KKZones;
import com.koolkidzmc.kkzones.utils.ColorAPI;
import com.koolkidzmc.kkzones.utils.FastInv;
import com.koolkidzmc.kkzones.utils.ItemBuilder;
import com.koolkidzmc.kkzones.utils.SoundAPI;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import java.util.Objects;

public class ServerSelectorGUI extends FastInv {
    private final FileConfiguration cfg;
    private final KKZones plugin;
    public static String currentServer;


    public ServerSelectorGUI(KKZones plugin, Player player) {
        super(27, ColorAPI.formatString("&dServer Selector"));
        this.plugin = plugin;
        this.cfg = plugin.getConfig();

        fillBackground();
        populateServerSlots();
        addNavigationButtons(player);
    }

    private void fillBackground() {
        for (int i = 0; i < 9; i++) {
            setItem(i, createBackgroundItem().build());
            setItem(i + 18, createBackgroundItem().build());
        }
        for (int i = 1; i < 2; i++) {
            setItem(i * 9, createBackgroundItem().build());
            setItem(i * 9 + 8, createBackgroundItem().build());
        }
    }
    private ItemBuilder createBackgroundItem() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .lore(ColorAPI.formatString("&8www.koolkidzmc.com"));
    }

    public void populateServerSlots() {
        //TODO: Get server stats and add it to gui.
    }

    private void addNavigationButtons(Player player) {
        setItem(18, new ItemBuilder(Material.BARRIER)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .name(ColorAPI.formatString("&c&lClose"))
                .addLore(ColorAPI.formatString("&7➜ Click to close"))
                .build(), e -> {
            SoundAPI.fail(player);
            Objects.requireNonNull(e.getClickedInventory()).close();
        });
    }
}
