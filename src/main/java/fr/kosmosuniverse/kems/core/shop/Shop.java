package fr.kosmosuniverse.kems.core.shop;

import fr.kosmosuniverse.kems.Kems;
import fr.kosmosuniverse.kems.utils.FileUtils;
import fr.kosmosuniverse.kems.utils.ItemMaker;
import org.bukkit.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.*;

/**
 * @author KosmosUniverse
 */
public class Shop {
    private static Shop instance;
    private IShop shop;
    private Map<String, Inventory> invs;
    private static final ItemStack limePane = new ItemMaker(Material.LIME_STAINED_GLASS_PANE, NamespacedKey.minecraft("kemsshoplimepane")).addQuantity(1).addName(" ").getItem();
    private static final ItemStack redPane = new ItemMaker(Material.RED_STAINED_GLASS_PANE, NamespacedKey.minecraft("kemsshopredpane")).addQuantity(1).addName("<- Back").getItem();
    private static final String MAIN_INV = "K.E.M.S Shop";
    private static final String PRICE = "price";
    private static final String KEMS_SHOP_ITEM = "kemsshopitem";

    public static synchronized Shop getInstance() {
        if (instance == null) {
            instance = new Shop();
        }

        return instance;
    }

    public void loadShops() {
        try {
            String rawValues = FileUtils.readFileContent(Kems.getInstance().getResource("shop.json"));
            JSONTokener tokenizer = new JSONTokener(rawValues);
            JSONObject menuObject = new JSONObject(tokenizer);
            shop = processMenu(MAIN_INV, menuObject);
        } catch (IOException e) {
            // TODO
        }

        invs = new HashMap<>();
        setupMenuInvs((ShopMenu) shop, null);
    }

    public IShop getShop() {
        return shop;
    }

    public boolean hasInv(String invName) {
        return invs.keySet().stream().anyMatch(key -> key.endsWith(invName));
    }

    public Inventory getMainInventory() {
        return getInventory(MAIN_INV);
    }

    public Inventory getInventory(String invName) {
        Optional<String> inv = invs.keySet().stream().filter(key -> key.endsWith(invName)).findAny();

        return inv.map(s -> invs.get(s)).orElse(null);
    }

    private IShop processMenu(String name, JSONObject menuObject) {
        String color = menuObject.has("color") ? menuObject.getString("color") : null;
        Material item = menuObject.has("item") ? Material.getMaterial(menuObject.getString("item").toUpperCase()) : null;
        int size = menuObject.has("size") ? menuObject.getInt("size") : 27;
        JSONObject menuContent = menuObject.getJSONObject("content");

        ShopMenu menu = new ShopMenu(name, color, item, size);

        for (String key : menuContent.keySet()) {
            JSONObject object = menuContent.getJSONObject(key);

            if (object.getString("type").equals("Menu")) {
                menu.addContent(processMenu(key, object));
            } else if (object.getString("type").equals("Item")) {
                try {
                    menu.addContent(processItem(key, object));
                } catch (IllegalArgumentException e) {
                    // Log item Error
                }
            } else if (object.getString("type").equals("Effect")) {
                try {
                    menu.addContent(processEffect(key, object));
                } catch (IllegalArgumentException e) {
                    // Log Effect Error
                }
            }
        }

        return menu;
    }

    private IShop processItem(String material, JSONObject itemObject) throws IllegalArgumentException {
        String[] options = null;

        if (material.contains("/")) {
            material = material.split("/")[0];
        } else if (material.contains("!")) {
            options = material.split("!");
            material = options[0];
        }

        Material mat = Material.getMaterial(material.toUpperCase());
        String name = itemObject.has("name") ? itemObject.getString("name") : null;
        String lore = itemObject.has("lore") ? itemObject.getString("lore") : null;
        if (!itemObject.has(PRICE)) {
            throw new IllegalArgumentException("No price tag");
        }
        int price = itemObject.getInt(PRICE);
        int quantity = itemObject.has("quantity") ? itemObject.getInt("quantity") : 1;
        int durability = itemObject.has("durability") ? itemObject.getInt("durability") : 0;
        String enchants = itemObject.has("enchants") ? itemObject.getString("enchants") : null;

        List<ShopItemTags> tags = new ArrayList<>();

        if (itemObject.has("customs")) {
            JSONArray array = itemObject.getJSONArray("customs");

            array.forEach(o -> {
                JSONObject tag = (JSONObject) o;

                if ("INTEGER".equals(tag.getString("type"))) {
                    tags.add(new ShopItemTags(tag.getString("key"), tag.getString("type"), tag.get("value")));
                } else if ("BOOLEAN".equals(tag.getString("type"))) {
                    tags.add(new ShopItemTags(tag.getString("key"), tag.getString("type"), tag.get("value")));
                }
            });
        }

        return new ShopItem(mat, name, lore, price, quantity, durability, enchants, options, tags);
    }

    private IShop processEffect(String effect, JSONObject effectObject) throws IllegalArgumentException {
        if (effect.contains("/")) {
            effect = effect.split("/")[0];
        }

        String name = effectObject.has("name") ? effectObject.getString("name") : null;
        String lore = effectObject.has("lore") ? effectObject.getString("lore") : null;
        if (!effectObject.has(PRICE)) {
            throw new IllegalArgumentException("No price tag");
        }
        int price = effectObject.getInt(PRICE);
        int level = effectObject.has("level") ? effectObject.getInt("level") : 1;
        int duration = effectObject.has("duration") ? effectObject.getInt("duration") : 1;

        return new ShopEffect(effect, name, lore, price, level, duration);
    }

    private void setupMenuInvs(ShopMenu menu, String prevInv) {
        String name = (menu.getColor() == null ? ChatColor.BLACK : menu.getColor()) + menu.getName();
        Inventory inv = Bukkit.createInventory(null, menu.getSize(), name);
        int contentAmnt = menu.getContent().size();
        int contentCnt = 0;

        for (int i = 0; i < menu.getSize(); i++) {
            if (i == 0 && prevInv != null) {
                inv.setItem(i, redPaneWithLore(prevInv));
            } else if (i < 9 || i >= (menu.getSize()) - 9) {
                inv.setItem(i, limePane);
            } else if (contentCnt < contentAmnt) {
                IShop menuContent = menu.getContent().get(contentCnt);

                if (menuContent.getType() == EShopType.MENU) {
                    inv.setItem(i, createMenuItem((ShopMenu) menuContent));
                    setupMenuInvs((ShopMenu) menuContent, menu.getName());
                } else if (menuContent.getType() == EShopType.ITEM) {
                    inv.setItem(i, createItem((ShopItem) menuContent));
                } else if (menuContent.getType() == EShopType.EFFECT) {
                    inv.setItem(i, createEffect((ShopEffect) menuContent));
                }

                contentCnt++;
            }
        }

        invs.put(name, inv);
    }

    private ItemStack redPaneWithLore(String name) {
        ItemStack item = redPane.clone();
        ItemMeta itM = item.getItemMeta();

        Objects.requireNonNull(itM).setLore(Collections.singletonList(name));
        item.setItemMeta(itM);

        return item;
    }

    private ItemStack createMenuItem(ShopMenu menu) {
        return new ItemMaker(menu.getItem(), NamespacedKey.minecraft(KEMS_SHOP_ITEM)).addQuantity(1).addName((menu.getColor() == null ? ChatColor.BLACK : menu.getColor()) + menu.getName()).getItem();
    }

    private ItemStack createItem(ShopItem item) {
        ItemStack i = new ItemMaker(item.getMaterial(), NamespacedKey.minecraft(KEMS_SHOP_ITEM)).addQuantity(item.getQuantity()).addName(item.getName()).setLores(item.getLore()).addLore("Price: " + item.getPrice() + " points").addDurability(item.getDurability()).addEnchants(item.getEnchants()).addTags(item.getTags()).getItem();

        if (item.getOptions() != null) {
            processItemEffects(i, item.getOptions());
        }

        return i;
    }

    private void processItemEffects(ItemStack item, String[] options) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        for (int i = 1; i < options.length; i++) {
            String[] effect = options[i].split("-");

            Objects.requireNonNull(meta).addCustomEffect(new PotionEffect(Objects.requireNonNull(Registry.EFFECT.match(effect[0])), Integer.parseInt(effect[2]), Integer.parseInt(effect[1]), false, true, true), true);
        }

        item.setItemMeta(meta);
    }

    private ItemStack createEffect(ShopEffect effect) {
        ItemStack potion = new ItemMaker(Material.POTION, NamespacedKey.minecraft(KEMS_SHOP_ITEM)).addQuantity(1).addName(effect.getName()).setLores(effect.getLore()).addLore("Price: " + effect.getPrice() + " points").getItem();
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();

        Objects.requireNonNull(potionMeta).addCustomEffect(new PotionEffect(effect.getEffect(), effect.getDuration(), effect.getLevel(), false, true, true), true);
        potion.setItemMeta(potionMeta);

        return potion;
    }
}
