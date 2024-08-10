package fr.kosmosuniverse.kems.listeners;

import fr.kosmosuniverse.kems.core.GameManager;
import fr.kosmosuniverse.kems.core.PlayersList;
import fr.kosmosuniverse.kems.core.Status;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author KosmosUniverse
 */
public class PlayerInteractions implements Listener {
    @EventHandler
    public void onShulkerBoxOpen(PlayerInteractEvent event) {
        if (GameManager.getInstance().getGameStatus() == Status.NOT_LAUNCHED) {
            return ;
        }

        Player player = event.getPlayer();

        if (!PlayersList.getInstance().hasPlayer(player.getName())) {
            return ;
        }

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_BLOCK) {
            return ;
        }

        Block block = event.getClickedBlock();

        if (Objects.requireNonNull(block).getType() != Material.SHULKER_BOX) {
            return ;
        }

        ShulkerBox data = (ShulkerBox) block.getState();

        if ("NEW RANK !".equals(Objects.requireNonNull(data.getCustomName()))) {
            event.setCancelled(true);

            for (ItemStack item : data.getInventory().getContents()) {
                if (item != null) {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
            }

            block.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onPlayerRepair(PrepareItemCraftEvent event) {
        if (GameManager.getInstance().getGameStatus() == Status.NOT_LAUNCHED) {
            return ;
        }

        List<Player> viewers =  event.getViewers().stream().filter(Player.class::isInstance).map(Player.class::cast).collect(Collectors.toList());

        if (viewers.isEmpty() || viewers.stream().noneMatch(player -> PlayersList.getInstance().hasPlayer(player.getName()))) {
            return ;
        }

        if (event.isRepair()) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onPlayerUseAnvil(PrepareAnvilEvent event) {
        if (GameManager.getInstance().getGameStatus() == Status.NOT_LAUNCHED) {
            return ;
        }

        List<Player> viewers =  event.getViewers().stream().filter(Player.class::isInstance).map(Player.class::cast).collect(Collectors.toList());

        if (viewers.isEmpty() || viewers.stream().noneMatch(player -> PlayersList.getInstance().hasPlayer(player.getName()))) {
            return ;
        }

        event.getInventory().setRepairCost(4000);
    }
}
