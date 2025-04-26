package fr.kosmosuniverse.kems.utils;

import fr.kosmosuniverse.kems.core.Config;

public class PointsCalculatorUtils {
    enum Phase {
        BONUS,
        BASIC,
        DECREASING
    }
    private static final double totalPointsCoefMax = Config.getInstance().getConfigValues().getPointLimit() / 400d;
    private static final double totalPointsCoef = Config.getInstance().getConfigValues().getPointLimit() / 1000d;
    private static final int maxMobAmnt = 78;/*Mobs.getInstance().getEntityAmount();*/

    private PointsCalculatorUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static int calculatePoint(int mobTypeCount, int mobPoints, int mobKillCount, int totalPlayerKill, int playerPoints) {
        double mobTypeCoef = 100d * mobTypeCount / maxMobAmnt;
        double totalCoef = (totalPointsCoefMax + mobTypeCoef) / 2d;
        double pointsCoef = (double) playerPoints / Config.getInstance().getConfigValues().getPointLimit();
        double killCoef = (double) mobKillCount / totalPlayerKill;
        double mobMaxPoints = mobPoints + ((mobPoints * totalCoef) / 100d);
        double mobPointToRetrieve;
        int retMobPoints;

        if (mobKillCount <= (int) totalPointsCoef) {
            mobPointToRetrieve = ((mobMaxPoints - mobPoints) / totalPointsCoef) * (mobKillCount - 1);
            retMobPoints = (int) (mobMaxPoints - Math.floor(mobPointToRetrieve));
        } else if (mobKillCount <= totalPointsCoef * 2) {
            retMobPoints = mobPoints;
        } else {
            mobPointToRetrieve = ((pointsCoef + killCoef) / 2) * mobPoints;
            retMobPoints = (int) (mobPoints - Math.ceil(mobPointToRetrieve));
        }

        return retMobPoints <= 0 ? 1 : retMobPoints;
    }

    public static Phase getPhase(int killCount) {
        if (killCount <= totalPointsCoef) {
            return Phase.BONUS;
        } else if (killCount <= totalPointsCoef * 2) {
            return  Phase.BASIC;
        } else {
            return Phase.DECREASING;
        }
    }
}
