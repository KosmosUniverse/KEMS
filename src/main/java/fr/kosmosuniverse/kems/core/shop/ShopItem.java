package fr.kosmosuniverse.kems.core.shop;

import fr.kosmosuniverse.kems.utils.ItemEnchant;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author KosmosUniverse
 */
@Getter
@Setter
public class ShopItem implements IShop {
    private final Material material;
    private final String name;
    private final List<String> lore;
    private final int price;
    private final int quantity;
    private final int durability;
    private List<ItemEnchant> enchants = null;
    private final String[] options;
    private final List<ShopItemTags> tags;

    public ShopItem(Material material, String name, String lore, int price, int quantity, int durability, String enchants, String[] options, List<ShopItemTags> tags) {
        this.material = material;
        this.name = (name == null || name.equals("")) ? null : name;
        this.lore = (lore == null || lore.equals("")) ? null : Arrays.asList(lore.split("-"));
        this.price = price;
        this.quantity = quantity;
        this.durability = durability;
        this.options = options;
        this.tags = tags.isEmpty() ? null : tags;

        if (enchants != null && !enchants.equals("")) {
            processEnchants(enchants);
        }
    }

    private void processEnchants(String raw) {
        List<String> rawEnchants = raw.contains("/") ? Arrays.asList(raw.split("/")) : Collections.singletonList(raw);

        this.enchants = new ArrayList<>();
        rawEnchants.forEach(re -> {
            String[] reSplitted = re.split("-");

            enchants.add(new ItemEnchant(Registry.ENCHANTMENT.match(reSplitted[0].toUpperCase()), Integer.parseInt(reSplitted[1])));
        });
    }

    @Override
    public EShopType getType() {
        return EShopType.ITEM;
    }
}
