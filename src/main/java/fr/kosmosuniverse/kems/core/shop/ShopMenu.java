package fr.kosmosuniverse.kems.core.shop;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KosmosUniverse
 */
@Getter
@Setter
public class ShopMenu implements IShop {
    private final String name;
    private final String color;
    private final Material item;
    private final int size;
    private final List<IShop> content;

    public ShopMenu(String name, String color, Material item, int size) {
        this.name = name;
        this.color = color;
        this.item = item;
        this.size = size;
        this.content = new ArrayList<>();
    }

    @Override
    public EShopType getType() {
        return EShopType.MENU;
    }

    public void addContent(IShop contentItem) {
        this.content.add(contentItem);
    }
}
