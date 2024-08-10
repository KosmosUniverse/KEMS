package fr.kosmosuniverse.kems.commands;

import fr.kosmosuniverse.kems.core.Config;
import fr.kosmosuniverse.kems.core.GameManager;
import fr.kosmosuniverse.kems.core.PlayersList;
import fr.kosmosuniverse.kems.core.Status;
import fr.kosmosuniverse.kems.core.shop.Shop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author KosmosUniverse
 */
public class KemsAdminPoints implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("kems-admin-points")) {
            return false;
        }

        if (GameManager.getInstance().getGameStatus() == Status.NOT_LAUNCHED) {
            player.sendMessage("[K.E.M.S] : Game is not launched");
            return true;
        }

        if (!PlayersList.getInstance().hasPlayer(args[0])) {
            player.sendMessage("[K.E.M.S] : Player " + args[0] + " is not in the game");

            return true;
        }

        if (args.length == 1) {
            player.sendMessage("[K.E.M.S] : Player " + args[0] + " currently have " + PlayersList.getInstance().getPlayerPoints(args[0]) + " points.");

            return true;
        } else if (args.length != 3) {
            return false;
        }

        int points = Integer.parseInt(args[2]);

        if ("add".equals(args[1])) {
            PlayersList.getInstance().addPlayerPoints(args[0], points);
            player.sendMessage("[K.E.M.S] : Player " + args[0] + " received " + points + " points.");
        } else if ("remove".equals(args[1])) {
            PlayersList.getInstance().removePlayerPoints(args[0], points);
            player.sendMessage("[K.E.M.S] : Player " + args[0] + " lost " + points + " points.");
        } else if ("set".equals(args[1])) {
            PlayersList.getInstance().setPlayerPoints(args[0], points);
            player.sendMessage("[K.E.M.S] : Player " + args[0] + " have now " + points + " points.");
        }

        return true;
    }
}
