package fr.kosmosuniverse.kems.utils;

import fr.kosmosuniverse.kems.core.shop.ShopItemTags;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author KosmosUniverse
 */
public class ItemMaker {
    @Getter
    final ItemStack item;

    public ItemMaker(Material material, NamespacedKey key) {
        item = new ItemStack(material);

        ItemMeta itM = item.getItemMeta();
        assert itM != null;
        itM.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(itM);
    }

    public ItemMaker addQuantity(int quantity) {
        item.setAmount(quantity);

        return this;
    }

    public ItemMaker addName(String name) {
        if (name == null) {
            return this;
        }

        ItemMeta itM = item.getItemMeta();

        assert itM != null;
        itM.setDisplayName(name);
        item.setItemMeta(itM);

        return this;
    }

    public ItemMaker setLores(List<String> lores) {
        ItemMeta itM = item.getItemMeta();

        assert itM != null;
        itM.setLore(new ArrayList<>());
        item.setItemMeta(itM);
        return addLores(lores);
    }

    public ItemMaker addLore(String lore) {
        if (lore == null) {
            return this;
        }

        ItemMeta itM = item.getItemMeta();

        assert itM != null;
        if (itM.getLore() == null) {
            itM.setLore(Collections.singletonList(lore));
        } else {
            List<String> l = itM.getLore();
            l.add(lore);

            itM.setLore(l);
        }

        item.setItemMeta(itM);

        return this;
    }

    public ItemMaker addLores(List<String> lores) {
        if (lores == null) {
            return this;
        }

        ItemMeta itM = item.getItemMeta();

        assert itM != null;
        if (itM.getLore() == null) {
            itM.setLore(lores);
        } else {
            List<String> l = itM.getLore();
            l.addAll(lores);

            itM.setLore(l);
        }

        item.setItemMeta(itM);

        return this;
    }

    public ItemMaker addEnchants(List<ItemEnchant> enchants) {
        if (enchants == null) {
            return this;
        }

        enchants.forEach(e -> item.addUnsafeEnchantment(e.getEnchant(), e.getLevel()));

        return this;
    }

    public ItemMaker addDurability(int durability) {
        if (durability != -1) {
            Damageable itM = (Damageable) item.getItemMeta();

            assert itM != null;
            itM.setDamage(item.getType().getMaxDurability() - durability);
            item.setItemMeta(itM);
        }

        return this;
    }

    public ItemMaker addTags(List<ShopItemTags> tags) {
        if (tags == null || tags.isEmpty()) {
            return this;
        }

        ItemMeta itM = item.getItemMeta();

        tags.forEach(tag -> {
            if ("INTEGER".equals(tag.getType())) {
                assert itM != null;
                itM.getPersistentDataContainer().set(tag.getKey(), PersistentDataType.INTEGER, (Integer) tag.getValue());
            } else if ("BOOLEAN".equals(tag.getType())) {
                assert itM != null;
                itM.getPersistentDataContainer().set(tag.getKey(), PersistentDataType.BOOLEAN, (Boolean) tag.getValue());
            }
        });

        item.setItemMeta(itM);

        return this;
    }
}
