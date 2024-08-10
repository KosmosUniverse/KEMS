package fr.kosmosuniverse.kems.commands;

import fr.kosmosuniverse.kems.core.GameManager;
import fr.kosmosuniverse.kems.core.Status;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author KosmosUniverse
 */
public class KemsResume implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        if (!player.hasPermission("kems-resume")) {
            player.sendMessage("You don't have access to this command.");
            return false;
        }

        if (GameManager.getInstance().getGameStatus() == Status.NOT_LAUNCHED) {
            player.sendMessage("[K.E.M.S] : Game is not launched");
            return true;
        }

        if (GameManager.getInstance().getGameStatus() == Status.LAUNCHED) {
            player.sendMessage("[K.E.M.S] : Game is already running");
        } else {
            GameManager.getInstance().resume();
        }

        return true;
    }
}
