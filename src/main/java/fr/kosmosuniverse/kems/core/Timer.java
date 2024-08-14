package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.Kems;
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
                }

                if (checkSpecialSpawn()) {
                    PlayersList.getInstance().triggerSpecialMobSpawn();
                }

                PlayersList.getInstance().sendPlayerActionBar(getTimeFromSec(getTimeElapsed() / 1000));
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
    public static String getTimeFromSec(long sec) {
        StringBuilder sb = new StringBuilder();

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
