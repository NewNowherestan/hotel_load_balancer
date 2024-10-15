package dev.stan.autostart;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

public class AppContext {
    private static AppContext context;

    private final Context pi4JContext;

    private long secs;
    private long msecs;

    public boolean isStarterRunning = false;
    public boolean isGeneratorWorking = false;
    public boolean isMainsPresent = false;
    public boolean isValveOpen = false;
    public boolean isValveOnBattery = false;

    public int attempts = 0;

    private AppContext(Context pi4JContext) {
        this.pi4JContext = pi4JContext;
        updateTimestamps();
    }

    public void updateTimestamps() {
        msecs = System.currentTimeMillis();
        secs = msecs / 1000;
    }


    public static AppContext getContext() {
        if (context == null) {
            context = new AppContext(Pi4J.newAutoContextAllowMocks());
        }

        return context;
    }

    public Context getPi4JContext() {
        return pi4JContext;
    }

    public long getSecs() {
        return secs;
    }

    public long getMsecs() {
        return msecs;
    }
}
