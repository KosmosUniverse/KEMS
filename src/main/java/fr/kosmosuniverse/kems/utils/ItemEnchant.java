package fr.kosmosuniverse.kems.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.enchantments.Enchantment;

/**
 * @author KosmosUniverse
 */
@Getter
@Setter
public class ItemEnchant {
    private final Enchantment enchant;
    private final int level;

    public ItemEnchant(Enchantment enchant, int level) {
        this.enchant = enchant;
        this.level = level;
    }
}
