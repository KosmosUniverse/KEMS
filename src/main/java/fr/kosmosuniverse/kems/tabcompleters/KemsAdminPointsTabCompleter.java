package fr.kosmosuniverse.kems.tabcompleters;

import fr.kosmosuniverse.kems.core.PlayersList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KosmosUniverse
 */
public class KemsAdminPointsTabCompleter implements TabCompleter {
    private final List<String> list = new ArrayList<>();

    public KemsAdminPointsTabCompleter() {
        list.add("add");
        list.add("remove");
        list.add("set");
    }
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        List<String> ret = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return PlayersList.getInstance().getPlayerList();
        } else if (args.length == 2) {
            return list;
        }

        return ret;
    }
}
