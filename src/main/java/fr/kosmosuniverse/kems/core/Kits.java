package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.Kems;
import fr.kosmosuniverse.kems.utils.FileUtils;
import fr.kosmosuniverse.kems.utils.ItemEnchant;
import fr.kosmosuniverse.kems.utils.ItemMaker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.*;

public class Kits {
    @Getter
    private static final String invName = "K.E.M.S Kits";
    @Getter
    private static final NamespacedKey KEMS_KIT_ITEM = NamespacedKey.minecraft("kemskititem");
    @Getter
    private static final NamespacedKey KEMS_KIT_POTION = NamespacedKey.minecraft("kemskitpotion");
    @Getter
    private static final NamespacedKey KEMS_KIT_ENCHANT = NamespacedKey.minecraft("kemskitenchant");
    private static final ItemStack limePane = ItemMaker.newItem(Material.LIME_STAINED_GLASS_PANE, NamespacedKey.minecraft("kemskitlimepane")).addQuantity(1).addName(" ").getItem();
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

                kit.addContent(processKitItem(kitItem));
            }

            kit.generateItem(KEMS_KIT_ITEM);
            kits.put(kitName, kit);
        }
    }

    private ItemStack processKitItem(JSONObject kitItemObj) {
        Material material = Material.getMaterial(kitItemObj.getString("type").toUpperCase());
        ItemMaker kitItem = ItemMaker.newItem(material, KEMS_KIT_ITEM);

        if (kitItemObj.has("amount")) {
            kitItem.addQuantity(kitItemObj.getInt("amount"));
        }

        if (kitItemObj.has("enchant") && kitItemObj.has("enchantLevel")) {
            kitItem.addEnchants(Collections.singletonList(new ItemEnchant(Registry.ENCHANTMENT.match("minecraft:" + kitItemObj.getString("enchant")), kitItemObj.getInt("enchantLevel"))));
            kitItem.addStringTag(KEMS_KIT_ENCHANT, kitItemObj.getString("enchant") + (kitItemObj.getInt("enchantLevel") > 1 ? " " + kitItemObj.getInt("enchantLevel") + " " : " ") + kitItemObj.getString("type").replace("_", " "));
        }

        if ((material == Material.POTION || material == Material.SPLASH_POTION) && kitItemObj.has("potionType") &&
                kitItemObj.has("potionPower") && kitItemObj.has("potionDuration")) {
            PotionMeta itM = (PotionMeta) kitItem.getItemMeta();

            itM.addCustomEffect(
                    new PotionEffect(Objects.requireNonNull(Registry.EFFECT.match("minecraft:" + kitItemObj.getString("potionType"))),
                    kitItemObj.getInt("potionDuration") * 20, kitItemObj.getInt("potionPower")), true);

            kitItem.setItemMeta(itM);
            kitItem.addStringTag(KEMS_KIT_POTION, kitItemObj.getString("potionType") + " potion level " + kitItemObj.getInt("potionPower") + " duration " + kitItemObj.getInt("potionDuration") + " seconds");
        }

        return kitItem.getItem();
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
