package fr.kosmosuniverse.kems.core.shop;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KosmosUniverse
 */
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

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Material getItem() {
        return item;
    }

    public int getSize() {
        return size;
    }

    public List<IShop> getContent() {
        return content;
    }

    public void addContent(IShop contentItem) {
        this.content.add(contentItem);
    }
}
