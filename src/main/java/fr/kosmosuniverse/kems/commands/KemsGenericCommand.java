package fr.kosmosuniverse.kems.commands;

import fr.kosmosuniverse.kems.Kems;
import fr.kosmosuniverse.kems.core.*;
import fr.kosmosuniverse.kems.core.shop.Shop;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author KosmosUniverse
 */
public class KemsGenericCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (!player.hasPermission(cmd.getName())) {
            player.sendMessage(Langs.getInstance().getMessage("noAccessToCmd"));
            return true;
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
            case "kems-kit" -> executekit(player);
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
            player.sendMessage(Langs.getInstance().getMessage("noAccessToCmd"));
            return true;
        }

        Map<String, String> mappedArgs = IntStream
                .range(0, args.length - 1)
                .boxed()
                .filter(idx -> Config.getInstance().hasKey(args[idx]))
                .collect(Collectors.toMap(i -> args[i], i -> args[i + 1]));

        mappedArgs.forEach((k, v) -> checkAndSetConfigValue(player, k, v));

        return true;
    }

    private boolean checkAndSetConfigValue(Player player, String key, String value) {
        if (Config.getInstance().setConfigValue(key, value)) {
            player.sendMessage(Langs.getInstance().getMessage("configValueSet").replace("%s", key));
        } else {
            player.sendMessage(Langs.getInstance().getMessage("configValueNotSet").replace("%s", key));
        }

        return true;
    }

    private boolean executeList(Player player, String[] args) {
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
        if (PlayersList.getInstance().getPlayers().isEmpty()) {
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
        if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED) {
            player.sendMessage("Game is not launched");
            return true;
        }

        GameManager.getInstance().stop();
        PlayersList.getInstance().reset();
        KemsKits.getInstance().clearKits();

        return true;
    }

    private boolean executePause(Player player) {
        if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED) {
            player.sendMessage(Langs.getInstance().getMessage("gameIsNotLaunched"));
            return true;
        }

        if (GameManager.getInstance().getStatus() == Status.PAUSED) {
            player.sendMessage(Langs.getInstance().getMessage("gameAlreadyPaused"));
        } else {
            GameManager.getInstance().pause();
        }

        return true;
    }

    private boolean executeResume(Player player) {
        if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED) {
            player.sendMessage(Langs.getInstance().getMessage("gameIsNotLaunched"));
            return true;
        }

        if (GameManager.getInstance().getStatus() == Status.LAUNCHED) {
            player.sendMessage(Langs.getInstance().getMessage("gameIsRunning"));
        } else {
            GameManager.getInstance().resume();
        }

        return true;
    }

    private boolean executeShop(Player player) {
        if (!Config.getInstance().getConfigValues().isShop()) {
            player.sendMessage(Langs.getInstance().getMessage("shopIsDisabled"));
            return true;
        }

        player.openInventory(Shop.getInstance().getMainInventory());

        return true;
    }

    private boolean executeAdminPoints(Player player, String[] args) {
        if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED) {
            player.sendMessage(Langs.getInstance().getMessage("gameIsNotLaunched"));
            return true;
        }

        if (!PlayersList.getInstance().hasPlayer(args[0])) {

            return true;
        }

        if (args.length == 1) {
            player.sendMessage(Langs.getInstance().getMessage("playerPoints").replace("%s", args[0]).replace("%i", "" + PlayersList.getInstance().getPlayerPoints(args[0])));

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

    private boolean executekit(Player player) {
        if (GameManager.getInstance().getStatus() == Status.LAUNCHED && PlayersList.getInstance().hasPlayer(player.getName())) {
            boolean kitWellDelivered = KemsKits.getInstance().canTakeKit(player.getUniqueId());

            if (kitWellDelivered) {
                player.openInventory(Kits.getInstance().getKitInv());
            } else {
                player.sendMessage(Langs.getInstance().getMessage("kitAlreadyTaken"));
            }
        } else {
            player.openInventory(Kits.getInstance().getKitInv());
        }

        return true;
    }
}
