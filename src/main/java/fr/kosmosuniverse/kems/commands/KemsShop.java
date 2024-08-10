package fr.kosmosuniverse.kems.commands;

import fr.kosmosuniverse.kems.core.Config;
import fr.kosmosuniverse.kems.core.shop.Shop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author KosmosUniverse
 */
public class KemsShop  implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("kems-shop")) {
            return false;
        }

        if (!Config.getInstance().getConfigValues().hasShop()) {
            player.sendMessage("[K.E.M.S] : Shop is disable for this game.");
            return true;
        }

        player.openInventory(Shop.getInstance().getMainInventory());

        return true;
    }
}
