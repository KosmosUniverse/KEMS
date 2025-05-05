package fr.kosmosuniverse.kems.listeners;

import fr.kosmosuniverse.kems.commands.KemsMobValues;
import fr.kosmosuniverse.kems.core.*;
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

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

/**
 * @author KosmosUniverse
 */
public class InventoryListener implements Listener {
    @EventHandler
    public void onShopInvClick(InventoryClickEvent event) {
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
            int price = Integer.parseInt(getLore(itM).getLast().split(" ")[1]);

            if (PlayersList.getInstance().canPlayerBuy(player, price)) {
                if (item.getType() == Material.POTION) {
                    player.addPotionEffect(((PotionMeta) item.getItemMeta()).getCustomEffects().get(0));
                } else {
                    LinkedList<String> lores = getLore(itM);

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

    @EventHandler
    public void onKitInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory current = event.getClickedInventory();
        ItemStack currentItem = event.getCurrentItem();
        String invName = event.getView().getTitle();

        if (currentItem == null || !Kits.getInvName().equals(invName)) {
            return ;
        }

        ItemStack item = currentItem.clone();

        boolean kemsKitItem = item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(NamespacedKey.minecraft("kemskititem"));
        boolean hasMeta = item.hasItemMeta();
        String itemName = hasMeta && Objects.requireNonNull(item.getItemMeta()).hasDisplayName() ? item.getItemMeta().getDisplayName() : null;

        event.setCancelled(true);

        if (Objects.requireNonNull(current).getHolder() != null) {
            return ;
        }

        if (kemsKitItem) {
            if (GameManager.getInstance().getStatus() == Status.NOT_LAUNCHED ||
                    !PlayersList.getInstance().hasPlayer(player.getName()) ||
                    !Kits.getInstance().hasKit(itemName)) {
                return ;
            }

            Optional<Kit> kit = Kits.getInstance().getKit(itemName);

            kit.ifPresent(value -> PlayersList.getInstance().giveKitToPlayer(player.getName(), value));
            player.closeInventory();
        }
    }

    @EventHandler
    public void onMobValueInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory current = event.getClickedInventory();
        ItemStack currentItem = event.getCurrentItem();
        String invName = event.getView().getTitle();

        if (currentItem == null || !KemsMobValues.getInvName().equals(invName)) {
            return;
        }

        ItemStack item = currentItem.clone();

        boolean kemsBackItem = item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(NamespacedKey.minecraft("kemsmobvaluebackpane"));
        boolean kemsQuitItem = item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(NamespacedKey.minecraft("kemsmobvaluequitpane"));
        boolean kemsNextItem = item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(NamespacedKey.minecraft("kemsmobvaluenextpane"));

        event.setCancelled(true);

        if (Objects.requireNonNull(current).getHolder() != null) {
            return;
        }

        if (kemsQuitItem) {
            player.closeInventory();
        } else if (kemsBackItem) {
            player.openInventory(KemsMobValues.getInstance().getPrevInv(player));
        } else if (kemsNextItem) {
            player.openInventory(KemsMobValues.getInstance().getNextInv(player));
        }
    }

    private LinkedList<String> getLore(ItemMeta itM) {
        return new LinkedList<>(Objects.requireNonNull(itM.getLore()));
    }
}
