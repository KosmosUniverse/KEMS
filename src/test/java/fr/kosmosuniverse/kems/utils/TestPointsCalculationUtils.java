package fr.kosmosuniverse.kems.utils;

import fr.kosmosuniverse.kems.core.Config;
import fr.kosmosuniverse.kems.core.Mobs;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

public class TestPointsCalculationUtils {
    @BeforeClass
    public static void before() {
        Config.getInstance().setupConfig(null);
        Config.getInstance().getConfigValues().setPointLimit(5000);
    }

    @AfterClass
    public static void after() {
        Mobs.getInstance().clear();
        Config.getInstance().clear();
    }

    @Test
    public void test() {
        int mob1KillCount = 0;
        int mob2KillCount = 0;
        int mob1Points = 43;
        int mob2Points = 5;
        int mob1Step = -1;
        int mob2Step = -1;
        Random rand = new Random();

        for (int i = 1; i < 51; i++) {
            if (rand.nextInt(2) == 0) {
                if (mob1Step == -1) {
                    mob1Step = (mob2Step == -1) ? 1 : 2;
                }
                mob1KillCount++;
                System.out.println("Zombie -> " + PointsCalculatorUtils.calculatePoint(mob1Step, mob1Points, mob1KillCount, i) + " points.");
            } else {
                if (mob2Step == -1) {
                    mob2Step = (mob1Step == -1) ? 1 : 2;
                }
                mob2KillCount++;
                System.out.println("Vache -> " + PointsCalculatorUtils.calculatePoint(mob2Step, mob2Points, mob2KillCount, i) + " points.");
            }
        }

        System.out.println("Zombie Kill Count : " + mob1KillCount);
        System.out.println("Vache Kill Count : " + mob2KillCount);
    }
}
