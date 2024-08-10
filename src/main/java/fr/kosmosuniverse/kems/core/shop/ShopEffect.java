package fr.kosmosuniverse.kems.core.shop;

import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

/**
 * @author KosmosUniverse
 */
public class ShopEffect implements IShop {
    private final PotionEffectType effect;
    private final String name;
    private final List<String> lore;
    private final int price;
    private final int level;
    private final int duration;

    public ShopEffect(String effect, String name, String lore, int price, int level, int duration) {
        this.effect = Registry.EFFECT.match(effect);
        this.name = (name == null || name.equals("")) ? null : name;
        this.lore = (lore == null || lore.equals("")) ? null : Arrays.asList(lore.split("-"));
        this.price = price;
        this.level = level;
        this.duration = duration * 20;
    }

    @Override
    public EShopType getType() {
        return EShopType.EFFECT;
    }

    public PotionEffectType getEffect() {
        return effect;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getPrice() {
        return price;
    }

    public int getLevel() {
        return level;
    }

    public int getDuration() {
        return duration;
    }
}
