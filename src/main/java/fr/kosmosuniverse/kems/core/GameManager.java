package fr.kosmosuniverse.kems.core;

import lombok.Getter;

/**
 * @author KosmosUniverse
 */
public class GameManager {
    private static GameManager instance = null;

    @Getter
    private Status status = Status.NOT_LAUNCHED;
    @Getter
    private Mode mode = Mode.NO_MODE;
    private final Timer timer;


    public GameManager() {
        timer = new Timer();
    }

    /**
     * Get GameManager instance
     *
     * @return GameManager instance
     */
    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }

        return instance;
    }

    public void chooseMode(Mode mode) {
        this.mode = mode;
    }

    public void launch() {
        if (status == Status.NOT_LAUNCHED && mode != Mode.NO_MODE) {
            status = Status.LAUNCHED;
            timer.startTimer();
            ScoreManager.getInstance().setup();
        }
    }

    public void pause() {
        if (status == Status.LAUNCHED) {
            timer.pauseTimer();
            status = Status.PAUSED;
        }
    }

    public void resume() {
        if (status == Status.PAUSED) {
            timer.resumeTimer();
            status = Status.LAUNCHED;
        }
    }

    public void stop() {
        if (status != Status.NOT_LAUNCHED) {
            timer.stopTimer();
            ScoreManager.getInstance().clear();
            PlayersList.getInstance().printRecap();
            PlayersList.getInstance().reset();
            status = Status.NOT_LAUNCHED;
        }
    }

    public void triggerGameEnd() {
        stop();
    }
}
