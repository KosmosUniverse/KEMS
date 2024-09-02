package fr.kosmosuniverse.kems;

import fr.kosmosuniverse.kems.commands.*;
import fr.kosmosuniverse.kems.core.*;
import fr.kosmosuniverse.kems.core.shop.Shop;
import fr.kosmosuniverse.kems.listeners.InventoryListener;
import fr.kosmosuniverse.kems.listeners.PlayerEvents;
import fr.kosmosuniverse.kems.listeners.PlayerInteractions;
import fr.kosmosuniverse.kems.listeners.PlayerKill;
import fr.kosmosuniverse.kems.tabcompleters.KemsAdminPointsTabCompleter;
import fr.kosmosuniverse.kems.tabcompleters.KemsConfigTabCompleter;
import fr.kosmosuniverse.kems.tabcompleters.KemsListTabCompleter;
import fr.kosmosuniverse.kems.tabcompleters.KemsSimpleTabCompleter;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**-
 * @author KosmosUniverse
 *
 * K.E.M.S plugin main class
 * Kosmos Encounter Mobs Survival
 */
public final class Kems extends JavaPlugin {
    @Getter
    private static Kems instance;
    private KemsConfigTabCompleter configTab = null;

    @Override
    public void onEnable() {
        instance = this;

        Config.getInstance().setupConfig(this.getConfig());

        if (!Langs.getInstance().loadLangs(Config.getInstance().getConfigValues().getLang())) {
             this.onDisable();
        }

        Mobs.getInstance().loadMobs();
        Shop.getInstance().loadShops();

        getServer().getPluginManager().registerEvents(new PlayerKill(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractions(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        Objects.requireNonNull(getCommand("kems-config")).setExecutor(new KemsGenericCommand());
        Objects.requireNonNull(getCommand("kems-list")).setExecutor(new KemsGenericCommand());
        Objects.requireNonNull(getCommand("kems-start")).setExecutor(new KemsGenericCommand());
        Objects.requireNonNull(getCommand("kems-stop")).setExecutor(new KemsGenericCommand());
        Objects.requireNonNull(getCommand("kems-pause")).setExecutor(new KemsGenericCommand());
        Objects.requireNonNull(getCommand("kems-resume")).setExecutor(new KemsGenericCommand());
        Objects.requireNonNull(getCommand("kems-shop")).setExecutor(new KemsGenericCommand());
        Objects.requireNonNull(getCommand("kems-admin-points")).setExecutor(new KemsGenericCommand());

        configTab = new KemsConfigTabCompleter();

        Objects.requireNonNull(getCommand("kems-config")).setTabCompleter(configTab);
        Objects.requireNonNull(getCommand("kems-list")).setTabCompleter(new KemsListTabCompleter());
        Objects.requireNonNull(getCommand("kems-start")).setTabCompleter(new KemsSimpleTabCompleter());
        Objects.requireNonNull(getCommand("kems-stop")).setTabCompleter(new KemsSimpleTabCompleter());
        Objects.requireNonNull(getCommand("kems-pause")).setTabCompleter(new KemsSimpleTabCompleter());
        Objects.requireNonNull(getCommand("kems-resume")).setTabCompleter(new KemsSimpleTabCompleter());
        Objects.requireNonNull(getCommand("kems-shop")).setTabCompleter(new KemsSimpleTabCompleter());
        Objects.requireNonNull(getCommand("kems-admin-points")).setTabCompleter(new KemsAdminPointsTabCompleter());
    }

    @Override
    public void onDisable() {
        if (GameManager.getInstance().getStatus() != Status.NOT_LAUNCHED) {
            GameManager.getInstance().stop();
        }

        PlayersList.getInstance().reset();
        Mobs.getInstance().clear();
        Config.getInstance().clear();

        if (configTab != null) {
            configTab.clear();
        }
    }
}
