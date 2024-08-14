package fr.kosmosuniverse.kems.listeners;

import fr.kosmosuniverse.kems.core.GameManager;
import fr.kosmosuniverse.kems.core.PlayersList;
import fr.kosmosuniverse.kems.core.Status;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * @author KosmosUniverse
 */
public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED) {
            return ;
        }

        Player player = event.getEntity();

        if (!PlayersList.getInstance().hasPlayer(player.getName())) {
            return ;
        }

        Location loc = player.getLocation();

        event.setKeepInventory(true);

        if (event.getDrops().size() > 0) {
            event.getDrops().clear();
        }

        PlayersList.getInstance().playerDied(player, loc);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED) {
            return ;
        }

        Player player = event.getPlayer();

        if (!PlayersList.getInstance().hasPlayer(player.getName())) {
            return ;
        }

        PlayersList.getInstance().playerRespawned(event, player);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED || !PlayersList.getInstance().hasPlayer(player.getName())) {
            return ;
        }

        PlayersList.getInstance().disconnectPlayer(player);
    }

    @EventHandler
    public void onPlayerReconnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED) {
            return ;
        }

        PlayersList.getInstance().reconnectPlayer(player);
    }
}
