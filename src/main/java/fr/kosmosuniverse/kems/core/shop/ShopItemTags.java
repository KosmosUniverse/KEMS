package fr.kosmosuniverse.kems.core.shop;

import org.bukkit.NamespacedKey;

/**
 * @author KosmosUniverse
 */
public class ShopItemTags {
    private final NamespacedKey key;
    private final String type;
    private final Object value;

    public ShopItemTags(String key, String type, Object value) {
        this.key = NamespacedKey.minecraft(key);
        this.type = type;
        this.value = value;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
