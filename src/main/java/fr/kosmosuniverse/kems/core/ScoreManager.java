package fr.kosmosuniverse.kems.core;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @author KosmosUniverse
 */
public class ScoreManager {
    private static ScoreManager instance = null;
    private final Scoreboard scoreboard;
    private Objective points;

    public ScoreManager() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public static synchronized ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }

        return instance;
    }

    public void setup() {
        points = scoreboard.registerNewObjective("points", Criteria.DUMMY, "Points");
        points.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        PlayersList.getInstance().getPlayers().forEach(pg -> {
            points.getScore(pg.getPlayerName()).setScore(0);
            pg.getPlayer().setScoreboard(scoreboard);
        });
    }

    public void clear() {
        if (points.getDisplaySlot() != null) {
            scoreboard.clearSlot(points.getDisplaySlot());
        }

        points.unregister();
        points = null;
    }

    public void setPlayerPoints(String playerName, int playerPoints) {
        points.getScore(playerName).setScore(playerPoints);
    }
}
