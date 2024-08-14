package fr.kosmosuniverse.kems.listeners;

import fr.kosmosuniverse.kems.core.GameManager;
import fr.kosmosuniverse.kems.core.PlayersList;
import fr.kosmosuniverse.kems.core.Status;
import org.bukkit.NamespacedKey;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

/**
 * @author KosmosUniverse
 */
public class PlayerKill implements Listener {
    @EventHandler
    protected void onPlayerKillMob(EntityDeathEvent event) {
        if (GameManager.getInstance().getStatus() != Status.LAUNCHED) {
            return ;
        }

        EntityType deadType = event.getEntity().getType();
        DamageSource source = event.getDamageSource();

        if (source.getCausingEntity() instanceof Player player) {
            Entity entity = event.getEntity();
            List<MetadataValue> metadatas = entity.getMetadata("SpecialMob");
            boolean isSpecial = false;

            if (!metadatas.isEmpty()) {
                isSpecial = metadatas.get(0).asBoolean();
            }

            ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
            boolean kemsItemPointBoost = Objects.requireNonNull(item).hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(NamespacedKey.minecraft("kemsitempointboost"));
            boolean kemsItemNoPointPenalty = Objects.requireNonNull(item).hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(NamespacedKey.minecraft("kemsitemnopointpenalty"));

            if (kemsItemPointBoost) {
                int pointBoost = item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.minecraft("kemsitempointboost"), PersistentDataType.INTEGER);

                PlayersList.getInstance().setPlayerPointBoost(player.getName(), pointBoost);
            }

            if (kemsItemNoPointPenalty) {
                boolean noPointPenalty = item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.minecraft("kemsitemnopointpenalty"), PersistentDataType.BOOLEAN);

                PlayersList.getInstance().setPlayerNoPointPointPenalty(player.getName(), noPointPenalty);
            }

            if (isSpecial) {
                PlayersList.getInstance().reportSpecialKill(player, entity);
            } else {
                PlayersList.getInstance().reportKill(player, deadType);
            }
        }
    }
}
