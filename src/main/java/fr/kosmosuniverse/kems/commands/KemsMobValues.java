package fr.kosmosuniverse.kems.commands;

import fr.kosmosuniverse.kems.core.Mob;
import fr.kosmosuniverse.kems.core.PlayerGame;
import fr.kosmosuniverse.kems.core.PlayersList;
import fr.kosmosuniverse.kems.utils.ItemMaker;
import fr.kosmosuniverse.kems.utils.PointsCalculatorUtils;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author KosmosUniverse
 */
public class KemsMobValues {
    @Getter
    private static final String invName = "K.E.M.S Next values";
    private static KemsMobValues instance;
    private static final Map<String, List<Inventory>> playerInvs = new HashMap<>();
    private static final ItemStack limePane = ItemMaker.newItem(Material.LIME_STAINED_GLASS_PANE, NamespacedKey.minecraft("kemsmobvaluelimepane")).addQuantity(1).addName(" ").getItem();
    private static final ItemStack backPane = ItemMaker.newItem(Material.RED_STAINED_GLASS_PANE, NamespacedKey.minecraft("kemsmobvaluebackpane")).addQuantity(1).addName("<- Back").getItem();
    private static final ItemStack quitPane = ItemMaker.newItem(Material.RED_STAINED_GLASS_PANE, NamespacedKey.minecraft("kemsmobvaluequitpane")).addQuantity(1).addName("<- Quit").getItem();
    private static final ItemStack nextPane = ItemMaker.newItem(Material.BLUE_STAINED_GLASS_PANE, NamespacedKey.minecraft("kemsmobvaluenextpane")).addQuantity(1).addName("Next ->").getItem();
    public static synchronized KemsMobValues getInstance() {
        if (instance == null) {
            instance = new KemsMobValues();
        }

        return instance;
    }

    public Inventory getPlayerMobValuesInventory(Player player) {
        playerInvs.remove(player.getName());

        List<Inventory> invs = new ArrayList<>();
        PlayerGame game = PlayersList.getInstance().getPlayer(player.getName());

        if (game.getKillCount() != 0) {
            boolean hasMoreInv = game.getMobOrder().size() > 45;
            AtomicInteger size = new AtomicInteger();
            size.set(((game.getMobOrder().size() / 9) + (game.getMobOrder().size() % 9 == 0 ? 0 : 1) + 1) * 9);

            final Inventory[] inv = {Bukkit.createInventory(null, Math.min(size.get(), 54), ChatColor.BLACK + invName + (hasMoreInv ? " - 1" : ""))};
            Map<EntityType, Integer> calculatedPoints = calculateNextPoints(game.getMobStats(), game.getMobOrder(), game.getKillCount(), game.getTotalPoints());

            AtomicInteger invIdx = new AtomicInteger();
            invIdx.set(9);
            setupFirstRow(inv[0], hasMoreInv);

            calculatedPoints.forEach((key, value) -> {
                inv[0].setItem(invIdx.get(),
                        ItemMaker.newItem(Material.matchMaterial(key + "_SPAWN_EGG"), NamespacedKey.minecraft("kemsmobpoints"))
                                .addName(ChatColor.BLUE + key.toString().replace('_', ' ').toLowerCase())
                                .addLore("Already killed : " + game.getMobStats().entrySet().stream().filter(m -> m.getKey().getType() == key).mapToInt(Map.Entry::getValue).sum() + " times")
                                .addLore("Phase : " + PointsCalculatorUtils.getPhase(game.getMobStats().entrySet().stream().filter(m -> m.getKey().getType() == key).mapToInt(Map.Entry::getValue).sum()))
                                .addLore("Next Point Value : " + value).getItem());
                invIdx.incrementAndGet();

                if (hasMoreInv && invIdx.get() == 54) {
                    invs.add(inv[0]);
                    inv[0] = Bukkit.createInventory(null, (size.get() - 54) + 9, ChatColor.BLACK + invName + " - 2");
                    setupFirstRow(inv[0], false);
                    invIdx.set(9);
                }
            });

            invs.add(inv[0]);
            playerInvs.put(player.getName(), invs);
            calculatedPoints.clear();
        }

        return invs.isEmpty() ? null : invs.get(0);
    }

    public Inventory getNextInv(Player player) {
        if (playerInvs.containsKey(player.getName()) && playerInvs.get(player.getName()).size() > 1) {
            return playerInvs.get(player.getName()).get(1);
        }

        return null;
    }

    public Inventory getPrevInv(Player player) {
        if (playerInvs.containsKey(player.getName()) && playerInvs.get(player.getName()).size() > 1) {
            return playerInvs.get(player.getName()).get(1);
        }

        return null;
    }

    private void setupFirstRow(Inventory inv, boolean hasMoreInv) {
        for (int i = 0; i < 9; i++) {
            if (i == 0) {
                inv.setItem(i, hasMoreInv ? quitPane : backPane);
            } else if (i <= 7) {
                inv.setItem(i, limePane);
            } else {
                inv.setItem(i, hasMoreInv ? nextPane : limePane);
            }
        }
    }

    private Map<EntityType, Integer> calculateNextPoints(Map<Mob, Integer> mobStats, List<EntityType> mobOrder, int playerkillCount, int playerPoints) {
        Map<EntityType, Integer> ret = new HashMap<>();

        mobStats.forEach((key, value) -> {
            int nextPoints = PointsCalculatorUtils.calculatePoint(mobOrder.indexOf(key.getType()),
                    key.getPoints(), value, playerkillCount, playerPoints);
            ret.put(key.getType(), nextPoints);
        });



        return ret;
    }
}
