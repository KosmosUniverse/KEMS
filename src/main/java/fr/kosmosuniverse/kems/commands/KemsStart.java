package fr.kosmosuniverse.kems.commands;

import fr.kosmosuniverse.kems.Kems;
import fr.kosmosuniverse.kems.core.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author KosmosUniverse
 */
public class KemsStart implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        if (!player.hasPermission("kems-start")) {
            player.sendMessage("You don't have access to this command.");
            return false;
        }

        if (PlayersList.getInstance().getPlayers().size() == 0) {
            player.sendMessage("Players list is empty.");
            return true;
        }

        if (GameManager.getInstance().getGameStatus() != Status.NOT_LAUNCHED) {
            player.sendMessage("Game is already running.");
            return true;
        }

        if (Config.getInstance().getConfigValues().getMode() == Mode.NO_MODE) {
            player.sendMessage("Game Mode have to be set to something but \"NO MODE\"");
            return true;
        }

        if (Config.getInstance().getConfigValues().isSpread()) {
            SpreadPlayer.spreadPlayers(player);
            PlayersList.getInstance().launch();
        } else {
            PlayersList.getInstance().launch(player.getLocation());
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Kems.getInstance(), () ->
            player.sendTitle(ChatColor.BOLD + String.valueOf(ChatColor.RED) + "5" + ChatColor.RESET, null, 5, 10, 5)
        , 20);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Kems.getInstance(), () ->
            player.sendTitle(ChatColor.BOLD + String.valueOf(ChatColor.GOLD) + "4" + ChatColor.RESET, null, 5, 10, 5)
        , 40);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Kems.getInstance(), () ->
            player.sendTitle(ChatColor.BOLD + String.valueOf(ChatColor.YELLOW) + "3" + ChatColor.RESET, null, 5, 10, 5)
        , 60);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Kems.getInstance(), () ->
            player.sendTitle(ChatColor.BOLD + String.valueOf(ChatColor.GREEN) + "2" + ChatColor.RESET, null, 5, 10, 5)
        , 80);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Kems.getInstance(), () ->
            player.sendTitle(ChatColor.BOLD + String.valueOf(ChatColor.BLUE) + "1" + ChatColor.RESET, null, 5, 10, 5)
        , 100);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Kems.getInstance(), () -> {
            player.sendTitle(ChatColor.BOLD + String.valueOf(ChatColor.DARK_PURPLE) + "GO!" + ChatColor.RESET, null, 5, 10, 5);
            GameManager.getInstance().chooseMode(Config.getInstance().getConfigValues().getMode());
            GameManager.getInstance().launch();
        }, 120);

        return true;
    }
}
