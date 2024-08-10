package fr.kosmosuniverse.kems.commands;

import fr.kosmosuniverse.kems.core.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


/**
 * @author KosmosUniverse
 */
public class KemsConfig implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        if (!(sender instanceof Player) || args.length % 2 == 1) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Config.getInstance().displayConfig());
            return true;
        }

        if (!player.hasPermission("kems-op")) {
            player.sendMessage("You don't have access to this command, please contact an administrator.");
            return true;
        }

        String key = "";

        for (int i = 0; i < args.length; i++) {
            if (i % 2 == 0) {
                key = args[i].toUpperCase();
            } else {
                if (!checkAndSetConfigValue(player, key, args[i])) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkAndSetConfigValue(Player player, String key, String value) {
        if (!Config.getInstance().hasKey(key)) {
            player.sendMessage("Config key \"" + key + "\" does not exists.");
            return false;
        } else {
            if (Config.getInstance().setConfigValue(key, value)) {
                player.sendMessage("Config value for \"" + key + "\" has been set.");
            } else {
                player.sendMessage("Config value for \"" + key + "\" has not been set.");
            }
        }

        return true;
    }
}
