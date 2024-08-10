package fr.kosmosuniverse.kems.commands;

import fr.kosmosuniverse.kems.core.GameManager;
import fr.kosmosuniverse.kems.core.PlayersList;
import fr.kosmosuniverse.kems.core.Status;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author KosmosUniverse
 */
public class KemsStop implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        if (!player.hasPermission("kems-stop")) {
            player.sendMessage("You don't have access to this command.");
            return false;
        }

        if (GameManager.getInstance().getGameStatus() == Status.NOT_LAUNCHED) {
            player.sendMessage("Game is not launched");
            return true;
        }

        GameManager.getInstance().stop();
        PlayersList.getInstance().reset();

        return true;
    }
}
