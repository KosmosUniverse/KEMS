package fr.kosmosuniverse.kems.utils;

import org.bukkit.enchantments.Enchantment;

/**
 * @author KosmosUniverse
 */
public class ItemEnchant {
    private Enchantment enchant;
    private int level;

    public ItemEnchant(Enchantment enchant, int level) {
        this.enchant = enchant;
        this.level = level;
    }

    public Enchantment getEnchant() {
        return enchant;
    }

    public int getLevel() {
        return level;
    }
}
