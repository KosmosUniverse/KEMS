package fr.kosmosuniverse.kems.utils;

import fr.kosmosuniverse.kems.core.Config;
import fr.kosmosuniverse.kems.core.Mobs;

public class PointsCalculatorUtils {
    private static final int stagPoint = Config.getInstance().getConfigValues().getPointLimit() / 500;
    private static final int maxMobAmnt = 78;/*Mobs.getInstance().getEntityAmount();*/

    private PointsCalculatorUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static int calculatePoint(int inputMobStep, int inputMobBasePoint, int inputMobKillCount, int inputPlayerKillCount) {
        double mobStep = (double) inputMobStep;
        double mobBasePoint = (double) inputMobBasePoint;
        double mobKillCount = (double) inputMobKillCount;
        double playerKillCount = (double) inputPlayerKillCount;

        double mobStepOnTotal = (1 + (mobStep / maxMobAmnt));
        double max_start = mobBasePoint * mobStepOnTotal;
        double decreaseCoef = -1 * (mobKillCount / playerKillCount) * (max_start / Math.pow(stagPoint, 3));
        double cubicPart = decreaseCoef * Math.pow(mobKillCount - stagPoint, 3) + max_start;
        double heavisidePart = (cubicPart + Math.abs(cubicPart)) / (2 * Math.abs(cubicPart));

        return Double.valueOf(cubicPart * (cubicPart != 0 ? heavisidePart : 1.0f) * mobStepOnTotal).intValue();
    }
}
