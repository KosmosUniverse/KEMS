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
public class KemsAdminPoints implements CommandExecutor {
    private static final String KEMS_PLAYER = "[K.E.M.S] : Player ";
    private static final String POINTS = " points.";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, @NotNull String[] args) {
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
            player.sendMessage(KEMS_PLAYER + args[0] + " is not in the game");

            return true;
        }

        if (args.length == 1) {
            player.sendMessage(KEMS_PLAYER + args[0] + " currently have " + PlayersList.getInstance().getPlayerPoints(args[0]) + POINTS);

            return true;
        } else if (args.length != 3) {
            return false;
        }

        int points = Integer.parseInt(args[2]);

        switch (args[1]) {
            case "add":
                PlayersList.getInstance().addPlayerPoints(args[0], points);
                player.sendMessage(KEMS_PLAYER + args[0] + " received " + points + POINTS);
                break;
            case "remove":
                PlayersList.getInstance().removePlayerPoints(args[0], points);
                player.sendMessage(KEMS_PLAYER + args[0] + " lost " + points + POINTS);
                break;
            case "set":
                PlayersList.getInstance().setPlayerPoints(args[0], points);
                player.sendMessage(KEMS_PLAYER + args[0] + " have now " + points + POINTS);
                break;
            default:
                return false;
        }

        return true;
    }
}
