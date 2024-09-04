package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.utils.ItemMaker;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

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
        ItemMaker tmpItem = new ItemMaker(Material.SHULKER_BOX, key).addName(name).addLore(lore).addLore("Content :");

        content.forEach(item -> tmpItem.addLore(" - " + item.getType().toString().toLowerCase().replace("_", " ") + " x" + item.getAmount()));
        this.item = tmpItem.getItem();
    }
}
