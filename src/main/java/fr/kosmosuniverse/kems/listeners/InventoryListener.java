package fr.kosmosuniverse.kems.listeners;

import fr.kosmosuniverse.kems.core.GameManager;
import fr.kosmosuniverse.kems.core.Langs;
import fr.kosmosuniverse.kems.core.PlayersList;
import fr.kosmosuniverse.kems.core.Status;
import fr.kosmosuniverse.kems.core.shop.Shop;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.List;
import java.util.Objects;

/**
 * @author KosmosUniverse
 */
public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        ItemStack currentItem = event.getCurrentItem();
        Inventory current = event.getClickedInventory();
        String invName = event.getView().getTitle();

        if (currentItem == null || !Shop.getInstance().hasInv(invName)) {
            return ;
        }

        ItemStack item = currentItem.clone();

        boolean kemsShopItem = item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(NamespacedKey.minecraft("kemsshopitem"));
        boolean kemsBackItem = item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(NamespacedKey.minecraft("kemsshopredpane"));
        boolean hasMeta = item.hasItemMeta();
        String itemName = hasMeta && Objects.requireNonNull(item.getItemMeta()).hasDisplayName() ? item.getItemMeta().getDisplayName() : null;

        event.setCancelled(true);

        if (Objects.requireNonNull(current).getHolder() != null) {
            return ;
        }

        if (kemsShopItem && Shop.getInstance().hasInv(item.getItemMeta().getDisplayName())) {
            player.openInventory(Shop.getInstance().getInventory(itemName));
        } else if (kemsBackItem) {
            ItemMeta itM = item.getItemMeta();
            String prevInvName = Objects.requireNonNull(itM.getLore()).get(0);

            if (Shop.getInstance().hasInv(prevInvName)) {
                player.openInventory(Shop.getInstance().getInventory(prevInvName));
            }
        } else if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasLore()) {
            if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED ||
                    !PlayersList.getInstance().hasPlayer(player.getName())) {
                return ;
            }

            ItemMeta itM = item.getItemMeta();
            int price = Integer.parseInt(Objects.requireNonNull(itM.getLore()).getLast().split(" ")[1]);

            if (PlayersList.getInstance().canPlayerBuy(player, price)) {
                if (item.getType() == Material.POTION) {
                    player.addPotionEffect(((PotionMeta) item.getItemMeta()).getCustomEffects().get(0));
                } else {
                    List<String> lores = itM.getLore();

                    lores.removeLast();
                    itM.setLore(lores);
                    item.setItemMeta(itM);

                    player.getInventory().addItem(item);
                }

                PlayersList.getInstance().playerBought(player, price);
            } else {
                player.sendMessage(Langs.getInstance().getMessage("notEnoughPoints"));
            }
        }
    }
}
