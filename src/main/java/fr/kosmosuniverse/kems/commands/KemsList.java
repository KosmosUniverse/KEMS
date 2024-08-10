package fr.kosmosuniverse.kems.commands;

import fr.kosmosuniverse.kems.core.PlayersList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * @author KosmosUniverse
 */
public class KemsList implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("kems-list")) {
            return false;
        }

        if (args.length == 0) {
            PlayersList.getInstance().displayList(player);

            return true;
        } else if (args.length == 1 && "reset".equals(args[0])) {
            PlayersList.getInstance().reset();
            player.sendMessage("[K.E.M.S] : Game list have been emptied.");

            return true;
        } else if (args.length != 2) {
            return false;
        }

        if ("add".equals(args[0])) {
            if ("@a".equals(args[1])) {
                PlayersList.getInstance().addPlayers(new ArrayList<>(Bukkit.getOnlinePlayers()));
                player.sendMessage("[K.E.M.S] : " + Bukkit.getOnlinePlayers().size() + " players have been added to the list.");
            } else {
                PlayersList.getInstance().addPlayer(args[1]);
                player.sendMessage("[K.E.M.S] : One player added to the list.");
            }
        } else if (args[0].equals("remove")) {
            PlayersList.getInstance().removePlayer(args[1]);
            player.sendMessage("[K.E.M.S] : " + args[1] + " have been removed from the list.");
        }

        return true;
    }
}
