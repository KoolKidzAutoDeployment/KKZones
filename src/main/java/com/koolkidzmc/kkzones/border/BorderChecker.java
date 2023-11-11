package com.koolkidzmc.kkzones.border;

import com.koolkidzmc.kkzones.utils.TaskManager;
import org.bukkit.entity.Player;

public class BorderChecker {
    public static void init() {
        TaskManager.Async.runTask(checkLoc, 5);
    }

    static Runnable checkLoc = () -> {

    };
}
