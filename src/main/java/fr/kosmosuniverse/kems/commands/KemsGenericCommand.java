package fr.kosmosuniverse.kems.commands;

import fr.kosmosuniverse.kems.Kems;
import fr.kosmosuniverse.kems.core.*;
import fr.kosmosuniverse.kems.core.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author KosmosUniverse
 */
public class KemsGenericCommand implements CommandExecutor {
    private static final String NO_ACCESS = "You don't have access to this command.";
    private static final String NOT_LAUNCHED = "[K.E.M.S] : Game is not launched.";
    private static final String KEMS_PLAYER = "[K.E.M.S] : Player ";
    private static final String POINTS = " points.";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        return switch (cmd.getName()) {
            case "kems-config" -> executeConfig(player, args);
            case "kems-list" -> executeList(player, args);
            case "kems-start" -> executeStart(player);
            case "kems-stop" -> executeStop(player);
            case "kems-pause" -> executePause(player);
            case "kems-resume" -> executeResume(player);
            case "kems-shop" -> executeShop(player);
            case "kems-admin-points" -> executeAdminPoints(player, args);
            default -> false;
        };
    }

    private boolean executeConfig(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(Config.getInstance().displayConfig());
            return true;
        } else if (args.length % 2 == 1) {
            return false;
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

    private boolean executeList(Player player, String[] args) {
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

        return switch (args[0]) {
            case "add" -> PlayersList.getInstance().add(player, args[1]);
            case "remove" -> PlayersList.getInstance().removePlayer(player, args[1]);
            default -> false;
        };
    }

    private boolean executeStart(Player player) {
        if (!player.hasPermission("kems-start")) {
            player.sendMessage(NO_ACCESS);
            return false;
        }

        if (PlayersList.getInstance().getPlayers().size() == 0) {
            player.sendMessage("Players list is empty.");
            return true;
        }

        if (GameManager.getInstance().getStatus() != Status.NOT_LAUNCHED) {
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

    private boolean executeStop(Player player) {
        if (!player.hasPermission("kems-stop")) {
            player.sendMessage(NO_ACCESS);
            return false;
        }

        if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED) {
            player.sendMessage("Game is not launched");
            return true;
        }

        GameManager.getInstance().stop();
        PlayersList.getInstance().reset();

        return true;
    }

    private boolean executePause(Player player) {
        if (!player.hasPermission("kems-pause")) {
            player.sendMessage(NO_ACCESS);
            return false;
        }

        if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED) {
            player.sendMessage(NOT_LAUNCHED);
            return true;
        }

        if (GameManager.getInstance().getStatus() == Status.PAUSED) {
            player.sendMessage("[K.E.M.S] : Game is already paused");
        } else {
            GameManager.getInstance().pause();
        }

        return true;
    }

    private boolean executeResume(Player player) {
        if (!player.hasPermission("kems-resume")) {
            player.sendMessage(NO_ACCESS);
            return false;
        }

        if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED) {
            player.sendMessage(NOT_LAUNCHED);
            return true;
        }

        if (GameManager.getInstance().getStatus() == Status.LAUNCHED) {
            player.sendMessage("[K.E.M.S] : Game is already running");
        } else {
            GameManager.getInstance().resume();
        }

        return true;
    }

    private boolean executeShop(Player player) {
        if (!player.hasPermission("kems-shop")) {
            return false;
        }

        if (!Config.getInstance().getConfigValues().isShop()) {
            player.sendMessage("[K.E.M.S] : Shop is disable for this game.");
            return true;
        }

        player.openInventory(Shop.getInstance().getMainInventory());

        return true;
    }

    private boolean executeAdminPoints(Player player, String[] args) {
        if (!player.hasPermission("kems-admin-points")) {
            return false;
        }

        if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED) {
            player.sendMessage(NOT_LAUNCHED);
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

        return switch (args[1]) {
            case "add" -> PlayersList.getInstance().addPlayerPoints(args[0], points, true);
            case "remove" -> PlayersList.getInstance().removePlayerPoints(args[0], points, true);
            case "set" -> PlayersList.getInstance().setPlayerPoints(args[0], points, true);
            default -> false;
        };
    }
}
