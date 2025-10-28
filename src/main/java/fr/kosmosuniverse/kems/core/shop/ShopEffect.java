package fr.kosmosuniverse.kems.core.shop;

import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * @author KosmosUniverse
 */
public record ShopEffect(PotionEffectType effect, String name, List<String> lore, int price, int level,
                         int duration) implements IShop {
    public ShopEffect {
        name = (name == null || name.isEmpty()) ? null : name;
        duration = duration * 20;
    }

    @Override
    public EShopType getType() {
        return EShopType.EFFECT;
    }
}
