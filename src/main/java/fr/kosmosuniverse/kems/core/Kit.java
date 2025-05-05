package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.utils.ItemMaker;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Kit {
    private final String name;
    private final String lore;
    private final List<ItemStack> content;
    private ItemStack item;

    public Kit(String name, String lore) {
        this.name = name;
        this.lore = lore;
        this.content = new ArrayList<>();
    }

    public void addContent(ItemStack item) {
        content.add(item);
    }

    public void clear() {
        content.clear();
    }

    public void generateItem(NamespacedKey key) {
        ItemMaker tmpItem = ItemMaker.newItem(Material.SHULKER_BOX, key).addName(name).addLore(lore).addLore("Content :");

        content.forEach(item -> {
            ItemMeta itM = item.getItemMeta();

            if (itM != null && itM.getPersistentDataContainer().has(Kits.getKEMS_KIT_POTION())) {
                tmpItem.addLore(" - " + item.getAmount() + "x " + Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().get(Kits.getKEMS_KIT_POTION(), PersistentDataType.STRING));
            } else if (itM != null && itM.getPersistentDataContainer().has(Kits.getKEMS_KIT_ENCHANT())) {
                tmpItem.addLore(" - " + item.getAmount() + "x " + Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().get(Kits.getKEMS_KIT_ENCHANT(), PersistentDataType.STRING));
            } else {
                tmpItem.addLore(" - " + item.getAmount() + "x " + item.getType().toString().toLowerCase().replace("_", " "));
            }
        });

        this.item = tmpItem.getItem();
    }
}
