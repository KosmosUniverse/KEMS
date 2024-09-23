package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.Kems;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author KosmosUniverse
 */
public class PlayersList {
    private static PlayersList instance = null;
    private List<PlayerGame> players = null;

    /**
     * Get PlayersList instance
     *
     * @return PlayersList instance
     */
    public static synchronized PlayersList getInstance() {
        if (instance == null) {
            instance = new PlayersList();
        }

        return instance;
    }

    /**
     * Get the players list
     *
     * @return The list of all players
     */
    public List<PlayerGame> getPlayers() {
        return players;
    }

    public int getPlayerQuantity() {
        return players.size();
    }

    /**
     * Checks if player list contains a specific player by its name
     *
     * @param playerName    The player name to search
     *
     * @return True if players is in the list, False instead
     */
    public boolean hasPlayer(String playerName) {
        return players.stream().anyMatch(p -> p.isConnected() && p.getPlayer().getName().equals(playerName));
    }

    public boolean add(Player sender, String p) {
        if (players == null) {
            players = new ArrayList<>();
        }

        if ("@a".equals(p)) {
            Bukkit.getOnlinePlayers().forEach(player -> this.players.add(new PlayerGame(player)));
            sender.sendMessage(Langs.getInstance().getMessage("playersAddedToList").replace("%i", "" + Bukkit.getOnlinePlayers().size()));
        } else {
            players.add(new PlayerGame(searchPlayerByName(p)));
            sender.sendMessage(Langs.getInstance().getMessage("playerAddedToList").replace("%s", p));
        }

        return true;
    }

    /**
     * Remove a player from the players list by its name
     *
     * @param player    the name of the player to remove
     */
    public boolean removePlayer(Player sender, String player) {
        if (players != null) {
            players.removeIf(p -> p.getPlayer().getName().equals(player));
            sender.sendMessage(Langs.getInstance().getMessage("playerRemovedFromList").replace("%s", player));
        }

        return true;
    }

    /**
     * Clear the players list
     */
    public void reset() {
        if (players != null) {
            players.forEach(PlayerGame::stop);
            players.clear();
        }
    }

    /**
     * Display the players list to a specific player
     *
     * @param toSend    The player to send the list
     */
    public void displayList(Player toSend) {
        if (players == null || players.isEmpty()) {
            toSend.sendMessage(Langs.getInstance().getMessage("playerListEmpty"));
            return ;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(Langs.getInstance().getMessage("playerListContent"));

        for (PlayerGame player : players) {
            sb.append(player.getPlayer().getName()).append("; ");
        }

        toSend.sendMessage(sb.toString());
    }

    public void printRecap() {
        players.forEach(this::printPlayer);
    }

    private void printPlayer(PlayerGame player) {
        players.forEach(toSend -> toSend.getPlayer().sendMessage(player.getRecap()));
    }

    /**
     * Search a player by its name in the online players
     *
     * @param name  The name of the plahyer to search
     *
     * @return The Player if found, null instead
     */
    private Player searchPlayerByName(String name) {
        List<Player> plyrs = new ArrayList<>(Bukkit.getOnlinePlayers());
        Player retPlayer = null;

        for (Player player : plyrs) {
            if (player.getName().contains(name)) {
                retPlayer = player;
            }
        }

        plyrs.clear();

        return retPlayer;
    }

    public List<String> getPlayerList() {
        return players.stream().map(pg -> pg.getPlayer().getName()).collect(Collectors.toList());
    }

    public void launch() {
        players.stream().filter(PlayerGame::isConnected).forEach(PlayerGame::launch);
    }

    public void launch(Location loc) {
        players.stream().filter(PlayerGame::isConnected).forEach(playerGame -> playerGame.setSpawnLoc(loc));
        players.stream().filter(PlayerGame::isConnected).forEach(PlayerGame::launch);
    }

    public void reconnectPlayer(Player player) {
        players.stream().filter(p -> !p.isConnected() && p.getPlayerName().equals(player.getName())).findFirst().ifPresent(PlayerGame::reconnect);
    }

    public void disconnectPlayer(Player player) {
        players.stream().filter(p -> p.isConnected() && p.getPlayer().equals(player)).findFirst().ifPresent(PlayerGame::disconnect);
    }

    public void playerDied(Player player, Location loc) {
        players.stream().filter(p -> p.isConnected() && p.getPlayer().equals(player)).findFirst().ifPresent(p -> p.death(loc));
    }

    public void playerRespawned(PlayerRespawnEvent event, Player player) {
        players.stream().filter(p -> p.isConnected() && p.getPlayer().equals(player)).findFirst().ifPresent(p -> {
            event.setRespawnLocation(p.getSpawnLoc());
            Bukkit.getScheduler().scheduleSyncDelayedTask(Kems.getInstance(), p::respawn, 20);
        });
    }

    /**
     * Spreads players at the defined locations
     *
     * @param locations	The locations where players will be teleported
     */
    public void spread(List<Location> locations) {
        for (int cnt = 0; cnt < players.size(); cnt++) {
            Location location = locations.get(cnt);

            location.setY(Objects.requireNonNull(location.getWorld()).getHighestBlockYAt(location) + 1);

            players.get(cnt).setSpawnLoc(location);
        }
    }

    public void sendPlayerActionBar(String time) {
        players.stream().filter(PlayerGame::isConnected).forEach(p -> p.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(ChatColor.GREEN + "You currently have " + p.getCurrentPoints() + " points. " + time).create()));
    }

    public void triggerSpecialMobSpawn() {
        players.stream().filter(PlayerGame::isConnected).forEach(PlayerGame::triggerSpecialMob);
    }

    public void reportKill(Player player, EntityType type) {
        players.stream().filter(p -> p.isConnected() && p.getPlayer().equals(player)).findFirst().ifPresent(p -> p.addKill(type));
    }

    public void reportSpecialKill(Player player, Entity entity) {
        players.stream().filter(p -> p.isConnected() && p.getPlayer().equals(player)).findFirst().ifPresent(p -> p.addSpecialKill(entity));
    }

    public boolean canPlayerBuy(Player player, int price) {
        PlayerGame pg = players.stream().filter(p -> p.isConnected() && p.getPlayer().equals(player)).findFirst().orElse(null);

        return pg != null && pg.canBuy(price);
    }

    public void playerBought(Player player, int price) {
        players.stream().filter(p -> p.isConnected() && p.getPlayer().equals(player)).findFirst().ifPresent(p -> p.bought(price));
    }

    public int getPlayerPoints(String playerName) {
        PlayerGame pg = players.stream().filter(p -> p.isConnected() && p.getPlayer().getName().equals(playerName)).findFirst().orElse(null);

        return pg == null ? -1 : pg.getCurrentPoints();
    }

    public boolean addPlayerPoints(String playerName, int points, boolean needAck) {
        players.stream().filter(p -> p.isConnected() && p.getPlayer().getName().equals(playerName)).findFirst().ifPresent(p -> {
            p.addPoints(points);
            if (needAck) {
                p.getPlayer().sendMessage(Langs.getInstance().getMessage("givePointsToPlayer").replace("%s", p.getPlayerName()).replace("%i", "" + points));
            }
        });

        return true;
    }

    public boolean removePlayerPoints(String playerName, int points, boolean needAck) {
        players.stream().filter(p -> p.isConnected() && p.getPlayer().getName().equals(playerName)).findFirst().ifPresent(p -> {
            p.removePoints(points);
            if (needAck) {
                p.getPlayer().sendMessage(Langs.getInstance().getMessage("takePointsFromPlayer").replace("%s", p.getPlayerName()).replace("%i", "" + points));
            }
        });

        return true;
    }

    public boolean setPlayerPoints(String playerName, int points, boolean needAck) {
        players.stream().filter(p -> p.isConnected() && p.getPlayer().getName().equals(playerName)).findFirst().ifPresent(p -> {
            p.setPoints(points);
            if (needAck) {
                p.getPlayer().sendMessage(Langs.getInstance().getMessage("setPlayerPoints").replace("%s", p.getPlayerName()).replace("%i", "" + points));
            }
        });

        return true;
    }

    public void setPlayerPointBoost(String playerName, int pointBoost) {
        players.stream().filter(p -> p.isConnected() && p.getPlayer().getName().equals(playerName)).findFirst().ifPresent(p -> p.setPointBoost(pointBoost));
    }

    public void setPlayerNoPointPointPenalty(String playerName, boolean noPointPenalty) {
        players.stream().filter(p -> p.isConnected() && p.getPlayer().getName().equals(playerName)).findFirst().ifPresent(p -> p.setNoPointPenalty(noPointPenalty));
    }

    public void giveKitToPlayer(String playerName, Kit kit) {
        players.stream().filter(p -> p.isConnected() && p.getPlayer().getName().equals(playerName)).findFirst().ifPresent(p -> p.receiveKit(kit));
    }

    public void sendGameRanking() {
        Comparator<PlayerGame> comp;

        if (Config.getInstance().getConfigValues().getMode() == Mode.FIRST_TO_RANK) {
            comp = Comparator.comparingInt((PlayerGame p) -> p.getRank().getPoints());
        } else if (Config.getInstance().getConfigValues().getMode() == Mode.FIRST_TO_LIMIT) {
            comp = Comparator.comparingInt(PlayerGame::getCurrentPoints);
        } else {
            comp = Comparator.comparingInt(PlayerGame::getCurrentPoints);
        }

        players.sort(comp);

        String ranking = buildRanking();

        players.forEach(p -> p.getPlayer().sendMessage(ranking));
    }

    private String buildRanking() {
        StringBuilder sb = new StringBuilder();

        sb.append(Langs.getInstance().getMessage("rankingTitle"));

        int i = 0;
        for (PlayerGame p : players) {
            sb.append(Langs.getInstance().getMessage("rankingPlayer").replace("%rank", "" + (i + 1)).replace("%name", p.getPlayerName()).replace("%points", "" + (Config.getInstance().getConfigValues().getMode() == Mode.FIRST_TO_RANK ? p.getTotalPoints() : p.getCurrentPoints())));
        }

        return sb.toString();
    }
}
