package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.Kems;
import fr.kosmosuniverse.kems.utils.FileUtils;
import fr.kosmosuniverse.kems.utils.ItemMaker;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Kits {
    @Getter
    private static final String invName = "K.E.M.S Kits";
    private static final NamespacedKey KEMS_KIT_ITEM = NamespacedKey.minecraft("kemskititem");
    private static final ItemStack limePane = new ItemMaker(Material.LIME_STAINED_GLASS_PANE, NamespacedKey.minecraft("kemsshoplimepane")).addQuantity(1).addName(" ").getItem();
    private static Kits instance;
    private final Map<String, Kit> kits;
    @Getter
    private final Inventory kitInv;

    public Kits() {
        this.kits = new HashMap<>();
        this.kitInv = Bukkit.createInventory(null, 27, invName);
    }

    public static synchronized Kits getInstance() {
        if (instance == null) {
            instance = new Kits();
        }

        return instance;
    }

    public void loadKits() {
        try {
            String rawValues = FileUtils.readFileContent(Kems.getInstance().getResource("kits.json"));
            JSONTokener tokenizer = new JSONTokener(rawValues);
            JSONObject kitsObject = new JSONObject(tokenizer);
            processKits(kitsObject);

            createKitsInventory();
        } catch (IOException e) {
            Bukkit.getLogger().severe(Langs.getInstance().getMessage("cannotLoadKits"));
        }
    }

    public void clear() {
        kits.forEach((k, v) -> v.clear());
        kits.clear();
    }

    public boolean hasKit(String name) {
        return kits.containsKey(name);
    }

    public Optional<Kit> getKit(String name) {
        if (!hasKit(name)) {
            return Optional.empty();
        }

        return Optional.ofNullable(kits.get(name));
    }

    private void processKits(JSONObject kitsObject) {
        for (String key : kitsObject.keySet()) {
            String kitName = Langs.getInstance().getMessage(key + "Name");
            String kitLore = Langs.getInstance().getMessage(key + "Lore");
            Kit kit = new Kit(kitName, kitLore);
            JSONArray kitContent = kitsObject.getJSONArray(key);

            for (Object kitRawItem : kitContent) {
                JSONObject kitItem = (JSONObject) kitRawItem;

                Material material = Material.getMaterial(kitItem.getString("type").toUpperCase());

                if (kitItem.has("amount")) {
                    kit.addContent(new ItemMaker(material, KEMS_KIT_ITEM).addQuantity(kitItem.getInt("amount")).getItem());
                } else {
                    kit.addContent(new ItemMaker(material, KEMS_KIT_ITEM).getItem());
                }
            }

            kit.generateItem(KEMS_KIT_ITEM);
            kits.put(kitName, kit);
        }
    }

    private void createKitsInventory() {
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17) {
                kitInv.setItem(i, limePane);
            }
        }

        int i = 9;

        for (String key : kits.keySet()) {
            kitInv.setItem(i, kits.get(key).getItem());
            i++;
        }
    }
}
