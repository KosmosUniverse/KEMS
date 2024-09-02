package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.Kems;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author KosmosUniverse
 */
public class Timer {
    private BukkitTask runnable;
    private long timeZero;
    private long timePaused;
    private long specialSpawnDelay;

    public void startTimer() {
        startTimer(System.currentTimeMillis());
    }

    private void startTimer(long baseTime) {
        timeZero = baseTime;
        specialSpawnDelay = Config.getInstance().getConfigValues().getSpecialSpawnDelay() * 60L;
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (GameManager.getInstance().getMode() == Mode.MOST_POINTS_DURATION && checkDuration(getTimeElapsed() / 1000)) {
                    GameManager.getInstance().triggerGameEnd();
                } else if (GameManager.getInstance().getMode() == Mode.MOST_POINTS_DURATION) {
                    PlayersList.getInstance().sendPlayerActionBar(getTimeFromSec(((Config.getInstance().getConfigValues().getTimeLimit() * 60000L) - getTimeElapsed()) / 1000));
                } else {
                    PlayersList.getInstance().sendPlayerActionBar(getTimeFromSec(getTimeElapsed() / 1000));
                }

                if (checkSpecialSpawn()) {
                    PlayersList.getInstance().triggerSpecialMobSpawn();
                }
            }
        }.runTaskTimer(Kems.getInstance(), 0, 20);
    }

    public void pauseTimer() {
        if (!runnable.isCancelled()) {
            runnable.cancel();
        }

        timePaused = System.currentTimeMillis() - timeZero;
    }

    public void resumeTimer() {
        startTimer(System.currentTimeMillis() - timePaused);
    }

    public void stopTimer() {
        if (!runnable.isCancelled()) {
            runnable.cancel();
        }
    }

    private boolean checkDuration(long timeElapsed) {
        return timeElapsed >= (Config.getInstance().getConfigValues().getTimeLimit() * 60L);
    }

    private boolean checkSpecialSpawn() {
        boolean ret = false;
        long actualDelay = (System.currentTimeMillis() - timeZero) / 1000L;

        if (actualDelay != 0 && actualDelay % specialSpawnDelay == 0) {
            ret = true;
        }

        return ret;
    }

    public long getTimeElapsed() {
        return System.currentTimeMillis() - timeZero;
    }

    /**
     * Get the time display from second amount
     *
     * @param sec	The amount of seconds to convert
     *
     * @return a String that represent time like "xxhxxmxxs"
     */
    private String getTimeFromSec(long sec) {
        StringBuilder sb = new StringBuilder();

        if (GameManager.getInstance().getMode() == Mode.MOST_POINTS_DURATION) {
            long maxDuration = Config.getInstance().getConfigValues().getTimeLimit() * 60000L;

            if (getTimeElapsed() <= maxDuration / 2) {
                sb.append(ChatColor.GREEN);
            } else if (getTimeElapsed() > (maxDuration - (maxDuration / 4))) {
                sb.append(ChatColor.RED);
            } else if (getTimeElapsed() > maxDuration / 2) {
                sb.append(ChatColor.YELLOW);
            }
        }

        if (sec >= 3600) {
            sb.append(sec / 3600);
            sb.append("h");

            sec = sec % 3600;
        }

        if (sec >= 60) {
            sb.append(sec / 60);
            sb.append("m");

            sec = sec % 60;
        }

        sb.append(sec);
        sb.append("s");

        return sb.toString();
    }
}
