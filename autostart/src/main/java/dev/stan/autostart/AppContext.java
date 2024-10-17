package dev.stan.autostart;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.registry.Registry;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppContext {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AppContext.class);
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

    public List<String[]> getSysParams() {
        List<String[]> sysParams = new ArrayList<>();
        sysParams.add(new String[]{"Starter running", String.valueOf(isStarterRunning)});
        sysParams.add(new String[]{"Generator working", String.valueOf(isGeneratorWorking)});
        sysParams.add(new String[]{"Mains present", String.valueOf(isMainsPresent)});
        sysParams.add(new String[]{"Valve open", String.valueOf(isValveOpen)});
        sysParams.add(new String[]{"Valve on battery", String.valueOf(isValveOnBattery)});
        sysParams.add(new String[]{"Attempts", String.valueOf(attempts)});

        return sysParams;
    }

    public List<String[]> getIO() {
        Registry pi4JRegistry = pi4JContext.registry();
        Map io = pi4JRegistry.all();
        List<String[]> ioList = new ArrayList<>();


        io.forEach((name, v) -> {
            if (v instanceof DigitalOutput) {
                DigitalOutput output = (DigitalOutput) v;
                ioList.add(new String[]{
                        "output",
                        String.valueOf(output.address()),
                        String.valueOf(output.state()) == "LOW" ? "0" : "1",
                        (String) name
                });
            } else if (v instanceof DigitalInput) {
                DigitalInput input = (DigitalInput) v;
                ioList.add(new String[]{
                        "input",
                        String.valueOf(input.address()),
                        String.valueOf(input.state()) == "LOW" ? "0" : "1",
                        (String) name
                });
            }
        });

        return ioList;
    }
}
