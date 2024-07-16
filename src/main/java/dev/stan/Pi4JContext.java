package dev.stan;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

public class Pi4JContext {
    private static Context context;

    public static Context getContext() {
        if (context == null) {
            context = Pi4J.newAutoContext();
        }
        return context;
    }
}
