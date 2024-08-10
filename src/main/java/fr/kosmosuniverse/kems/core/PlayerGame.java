package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.Kems;
import fr.kosmosuniverse.kems.utils.ItemMaker;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.ShulkerBox;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

/**
 * @author KosmosUniverse
 */
public class PlayerGame {
    private Player player;
    private final String playerName;
    private int totalPoints;
    private int currentPoints;
    private Ranks rank;
    private int killCount;
    private int deathCount;
    private Location deathLoc;
    private Location spawnLoc;
    private Inventory deathInv;
    private BossBar progress;
    private boolean connected;
    private List<ItemStack> futureReward;
    private final EnumMap<EntityType, Integer> mobStats;
    private int pointBoost = -1;

    public PlayerGame(Player player) {
        this.player = player;
        playerName = player.getName();
        rank = Ranks.SAND;
        totalPoints = 0;
        currentPoints = 0;
        killCount = 0;
        deathCount = 0;
        deathLoc = null;
        spawnLoc = null;
        deathInv = null;
        progress = null;
        connected = true;
        mobStats = new EnumMap<>(EntityType.class);
    }

    public void launch() {
        player.teleport(spawnLoc);
        progress = Bukkit.createBossBar(rank.getDisplayString(), BarColor.BLUE, BarStyle.SOLID);
        progress.addPlayer(player);
        futureReward = new ArrayList<>();

        updateProgress();
        updateListName();
    }

    public void stop() {
        player.setPlayerListName(playerName);
        progress.removePlayer(player);
        progress = null;

        if (futureReward != null) {
            futureReward.clear();
        }

        if (mobStats != null) {
            mobStats.clear();
        }
    }

    public String getRecap() {
        return getInfo(player.getName());
    }

    private String getInfo(String header) {

        return String.valueOf(ChatColor.GOLD) + ChatColor.BOLD + header + ":\n" + ChatColor.RESET +
                ChatColor.BLUE + "  - Rank: " + rank.getDisplayString() + "\n" +
                ChatColor.BLUE + "  - Points: " + totalPoints + "\n" +
                ChatColor.BLUE + "  - Kills: " + killCount + "\n" +
                ChatColor.BLUE + "  - Death: " + deathCount + "\n" + ChatColor.RESET;
    }

    public Player getPlayer() {
        return player;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    private void setCurrentPoints(int points) {
        this.currentPoints = points;
        ScoreManager.getInstance().setPlayerPoints(playerName, currentPoints);
    }

    public Ranks getRank() {
        return rank;
    }

    public void setRank(Ranks rank) {
        this.rank = rank;
    }

    public int getKillCount() {
        return killCount;
    }

    public int getPointBoost() {
        return pointBoost;
    }

    public void setPointBoost(int pointBoost) {
        this.pointBoost = pointBoost;
    }

    private void updateListName() {
        player.setPlayerListName("[" + rank.getDisplayString() + "] - " + playerName);
    }

    public void addSpecialKill(Entity entity) {
        int points = Mobs.getInstance().getMobPoints(entity.getType());

        addPointsAndRank(points + (points / 2));
    }

    private int totalMobStat() {
        int total = 0;

        for (int i : mobStats.values()) {
            total += i;
        }

        return total;
    }

    public void addKill(EntityType type) {
        if (mobStats.containsKey(type)) {
            mobStats.put(type, mobStats.get(type) + 1);
        } else {
            mobStats.put(type, 1);
        }

        int percentage = (mobStats.get(type) * 100) / totalMobStat();
        int points = Mobs.getInstance().getMobPoints(type);

        if (mobStats.get(type) > 10) {
            if (percentage > 50) {
                points = 0;
            } else if (percentage > 25) {
                points = points / 4;
            } else if (percentage > 10) {
                points = points / 2;
            }
        }

        addPointsAndRank(points);
    }

    private void addPointsAndRank(int points) {
        if (pointBoost != -1) {
            points += (points * pointBoost) / 100;
            pointBoost = -1;
        }

        totalPoints += points;
        setCurrentPoints(currentPoints + points);
        killCount++;

        boolean canRankPass = true;
        Ranks previousRank = rank;

        while (canRankPass) {
            canRankPass = checkAndUpdateRank();
        }

        if (rank != previousRank) {
            updateListName();
        }

        if ((GameManager.getInstance().getMode() == Mode.FIRST_TO_LIMIT &&
                currentPoints >= Config.getInstance().getConfigValues().getPointLimit()) ||
                (GameManager.getInstance().getMode() == Mode.FIRST_TO_RANK &&
                rank.getPoints() >= Config.getInstance().getConfigValues().getRankLimit().getPoints())) {
            GameManager.getInstance().triggerGameEnd();

            return ;
        }

        if (rank != previousRank && !futureReward.isEmpty()) {
            giveReward();
        }
    }

    private void giveReward() {
        ItemStack container = new ItemMaker(Material.SHULKER_BOX, NamespacedKey.minecraft("kemsreward")).addQuantity(1).addName("NEW RANK !").getItem();
        BlockStateMeta containerMeta = (BlockStateMeta) container.getItemMeta();
        ShulkerBox box = (ShulkerBox) Objects.requireNonNull(containerMeta).getBlockState();
        Inventory inv = box.getInventory();

        futureReward.forEach(inv::addItem);
        box.update();
        containerMeta.setBlockState(box);
        container.setItemMeta(containerMeta);

        futureReward.clear();

        Map<Integer, ItemStack> ret = player.getInventory().addItem(container);

        if (!ret.isEmpty()) {
            ret.forEach((key, value) -> player.getWorld().dropItem(player.getLocation(), value));
        }
    }

    public Location getSpawnLoc() {
        return spawnLoc;
    }

    public void setSpawnLoc(Location spawnLoc) {
        this.spawnLoc = spawnLoc;
    }

    public Location getDeathLoc() {
        return deathLoc;
    }

    public void death(Location loc) {
        deathCount++;
        deathLoc = loc;
        deathInv = Bukkit.createInventory(null, 54);

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                deathInv.addItem(item);
            }
        }

        player.getInventory().clear();
    }

    public void respawn() {
        Location loc = deathLoc;

        if (loc == null) {
            return;
        }

        if (Objects.requireNonNull(loc.getWorld()).getName().contains("the_end") && loc.getY() < 0) {
            changeLocForEnd(loc);
        }

        player.teleport(loc);

        for (ItemStack item : deathInv.getStorageContents()) {
            if (item != null) {
                player.getInventory().addItem(item);
            }
        }

        deathInv = null;
        deathLoc = null;

        int dp = Config.getInstance().getConfigValues().getDeathPenalty();

        if (dp != -1) {
            setCurrentPoints(dp > currentPoints ? 0 : currentPoints - dp);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void reconnect() {
        Bukkit.getOnlinePlayers().stream().filter(p -> Objects.requireNonNull(p.getPlayer()).getName().equals(playerName)).findFirst().ifPresent(p -> player = p);
        progress.addPlayer(player);
        connected = true;
    }

    public void disconnect() {
        connected = false;
    }

    public boolean canBuy(int price) {
        return currentPoints >= price;
    }

    public void bought(int price) {
        setCurrentPoints(currentPoints - price);
    }

    private void changeLocForEnd(Location loc) {
        int tmp = Objects.requireNonNull(loc.getWorld()).getHighestBlockYAt(loc);

        if (tmp != -1) {
            loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 1);
        } else {
            loc.setY(61);
        }
    }

    private boolean checkAndUpdateRank() {
        updateProgress();

        if (rank != Ranks.HEROBRINE && totalPoints >= rank.getNext().getPoints()) {
            rank = rank.getNext();
            futureReward.add(new ItemMaker(Material.COOKED_BEEF, NamespacedKey.minecraft("kemsrewardfood")).addQuantity(5).getItem());
            player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1f, 1f);

            return true;
        }

        return false;
    }

    private void updateProgress() {
        double calc;

        if (rank.getNext() == null) {
            calc = 1.0;
        } else {
            calc = (double) (totalPoints - rank.getPoints()) / (rank.getNext().getPoints() - rank.getPoints());
            calc = Math.min(calc, 1.0);

        }

        progress.setProgress(calc);
        progress.setTitle(rank.getDisplayString());
    }

    public void addPoints(int points) {
        setCurrentPoints(currentPoints + points);
    }

    public void removePoints(int points) {
        setCurrentPoints(points > currentPoints ? 0 : currentPoints - points);
    }

    public void setPoints(int points) {
        setCurrentPoints(points);
    }

    public void triggerSpecialMob() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Kems.getInstance(), () -> player.sendMessage("Special mob will spawn in 3..."), 20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Kems.getInstance(), () -> player.sendMessage("Special mob will spawn in 2..."), 40);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Kems.getInstance(), () -> player.sendMessage("Special mob will spawn in 1..."), 60);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Kems.getInstance(), () -> {
            player.sendMessage("Run.");
            player.getWorld().spawn(player.getLocation(), rank.getMobClass(), CreatureSpawnEvent.SpawnReason.CUSTOM, false, e -> {
                e.setMetadata("SpecialMob", new FixedMetadataValue(Kems.getInstance(), true));
                e.setMetadata("SpecialMobOwner", new FixedMetadataValue(Kems.getInstance(), playerName));
                rank.applyEntityStats(e);
            });
        }, 80);
    }
}
