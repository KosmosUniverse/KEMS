package fr.kosmosuniverse.kems.core.shop;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;

/**
 * @author KosmosUniverse
 */
@Getter
@Setter
public class ShopItemTags {
    private final NamespacedKey key;
    private final String type;
    private final Object value;

    public ShopItemTags(String key, String type, Object value) {
        this.key = NamespacedKey.minecraft(key);
        this.type = type;
        this.value = value;
    }
}
