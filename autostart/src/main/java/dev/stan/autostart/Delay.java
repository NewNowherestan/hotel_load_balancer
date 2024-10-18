package dev.stan.autostart;

public enum Delay {
    GENERATOR_COOLDOWN(10),
    STARTER_COOLDOWN(20),
    STARTER_ACTIVE_CYCLE(10),
    GAS_PREFLOW(5),
    ;

    private final int delay;

    private long startTimestampSecs = 0;

    Delay(int delay) {
        this.delay = delay;
    }

    public void start() {
        startTimestampSecs = AppContext.getContext().getSecs();
    }

    public int secondsLeft() {
        return delay - (int) (AppContext.getContext().getSecs() - startTimestampSecs);
    }

    public boolean isElapsed() {
        return AppContext.getContext().getSecs() - startTimestampSecs >= delay;
    }

    public void reset() {
        startTimestampSecs = 0;
    }

    public boolean isStarted() {
        return startTimestampSecs != 0;
    }

}
