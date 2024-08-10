package fr.kosmosuniverse.kems.commands;

import fr.kosmosuniverse.kems.core.GameManager;
import fr.kosmosuniverse.kems.core.Status;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author KosmosUniverse
 */
public class KemsPause implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        if (!player.hasPermission("kems-pause")) {
            player.sendMessage("You don't have access to this command.");
            return false;
        }

        if (GameManager.getInstance().getGameStatus() == Status.NOT_LAUNCHED) {
            player.sendMessage("[K.E.M.S] : Game is not launched");
            return true;
        }

        if (GameManager.getInstance().getGameStatus() == Status.PAUSED) {
            player.sendMessage("[K.E.M.S] : Game is already paused");
        } else {
            GameManager.getInstance().pause();
        }

        return true;
    }
}
