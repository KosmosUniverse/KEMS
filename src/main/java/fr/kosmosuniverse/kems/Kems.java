package fr.kosmosuniverse.kems;

import fr.kosmosuniverse.kems.commands.*;
import fr.kosmosuniverse.kems.core.*;
import fr.kosmosuniverse.kems.core.shop.Shop;
import fr.kosmosuniverse.kems.listeners.*;
import fr.kosmosuniverse.kems.tabcompleters.KemsAdminPointsTabCompleter;
import fr.kosmosuniverse.kems.tabcompleters.KemsConfigTabCompleter;
import fr.kosmosuniverse.kems.tabcompleters.KemsListTabCompleter;
import fr.kosmosuniverse.kems.tabcompleters.KemsSimpleTabCompleter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**-
 * @author KosmosUniverse
 *
 * K.E.M.S plugin main class
 * Kosmos Encounter Mobs Survival
 */
public final class Kems extends JavaPlugin {
    private static Kems instance;
    private KemsConfigTabCompleter configTab = null;

    @Override
    public void onEnable() {
        instance = this;

        Config.getInstance().setupConfig(this.getConfig());
        Mobs.getInstance().loadMobs();
        Shop.getInstance().loadShops();

        getServer().getPluginManager().registerEvents(new PlayerKill(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractions(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        Objects.requireNonNull(getCommand("kems-config")).setExecutor(new KemsConfig());
        Objects.requireNonNull(getCommand("kems-list")).setExecutor(new KemsList());
        Objects.requireNonNull(getCommand("kems-start")).setExecutor(new KemsStart());
        Objects.requireNonNull(getCommand("kems-stop")).setExecutor(new KemsStop());
        Objects.requireNonNull(getCommand("kems-pause")).setExecutor(new KemsPause());
        Objects.requireNonNull(getCommand("kems-resume")).setExecutor(new KemsResume());
        Objects.requireNonNull(getCommand("kems-shop")).setExecutor(new KemsShop());
        Objects.requireNonNull(getCommand("kems-admin-points")).setExecutor(new KemsAdminPoints());

        configTab = new KemsConfigTabCompleter();

        Objects.requireNonNull(getCommand("kems-config")).setTabCompleter(configTab);
        Objects.requireNonNull(getCommand("kems-list")).setTabCompleter(new KemsListTabCompleter());
        Objects.requireNonNull(getCommand("kems-start")).setTabCompleter(new KemsSimpleTabCompleter());
        Objects.requireNonNull(getCommand("kems-stop")).setTabCompleter(new KemsSimpleTabCompleter());
        Objects.requireNonNull(getCommand("kems-pause")).setTabCompleter(new KemsSimpleTabCompleter());
        Objects.requireNonNull(getCommand("kems-resume")).setTabCompleter(new KemsSimpleTabCompleter());
        Objects.requireNonNull(getCommand("kems-shop")).setTabCompleter(new KemsSimpleTabCompleter());
        Objects.requireNonNull(getCommand("kems-admin-points")).setTabCompleter(new KemsAdminPointsTabCompleter());


        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itM = item.getItemMeta();

        itM.getPersistentDataContainer().set(new NamespacedKey("namespaced", "key"), PersistentDataType.STRING, "it's me mario");

    }

    @Override
    public void onDisable() {
        if (GameManager.getInstance().getGameStatus() != Status.NOT_LAUNCHED) {
            GameManager.getInstance().stop();
        }

        PlayersList.getInstance().reset();
        Mobs.getInstance().clear();
        Config.getInstance().clear();

        if (configTab != null) {
            configTab.clear();
        }
    }

    public static Kems getInstance() {
        return instance;
    }
}
