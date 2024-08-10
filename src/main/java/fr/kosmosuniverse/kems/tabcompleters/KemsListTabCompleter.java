package fr.kosmosuniverse.kems.tabcompleters;

import fr.kosmosuniverse.kems.core.PlayerGame;
import fr.kosmosuniverse.kems.core.PlayersList;
import org.bukkit.Bukkit;
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
public class KemsListTabCompleter implements TabCompleter {
    private final List<String> list = new ArrayList<>();

    public KemsListTabCompleter() {
        list.add("add");
        list.add("remove");
        list.add("reset");
    }
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        List<String> ret = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return list;
        } else if (args.length == 2) {
            if (args[0].equals("add")) {
                ret.add("@a");

                for (Player player : Bukkit.getOnlinePlayers()) {
                    ret.add(player.getName());
                }
            } else if (args[0].equals("remove") && PlayersList.getInstance().getPlayers() != null) {
                for (PlayerGame player : PlayersList.getInstance().getPlayers()) {
                    ret.add(player.getPlayer().getName());
                }
            }
        }

        return ret;
    }
}
