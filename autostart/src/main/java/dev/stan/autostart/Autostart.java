package dev.stan.autostart;
import static dev.stan.autostart.InputPins.*;
import static dev.stan.autostart.OutputPIns.*;
import static dev.stan.autostart.PinState.*;

public class Autostart {

    //outputs - indicators
    // private static DigitalOutput genPowerIndicatorPin;
    // private static DigitalOutput mainsPowerIndicatorPin;

    //constants
    private static final int debounceMsecs = 150;
    private static final int allowedAttempts = 4;
    private static final long starterActiveDelaySecs = 10;
    
    private static long starterEnabledTimestampSecs = 0;

    private static long mainsInputValveChangedTimestampMsecs = 0;
    private static long genInputValueChangedTimestampMsecs = 0;




    private final static AppContext appContext = AppContext.getContext();
    private final static Generator generator = new Generator(appContext);   

    public static void main(String[] args) {
        // Main loop
        while (true) {
            loop();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void checkStatus() {
        boolean genPowerVal = GENERATOR_POWER.get();

        if (genPowerVal != appContext.isGeneratorWorking) {
            
            if (genInputValueChangedTimestampMsecs == 0) {
                genInputValueChangedTimestampMsecs = appContext.getMsecs();
            } else if (appContext.getMsecs() - genInputValueChangedTimestampMsecs >= debounceMsecs) {

                appContext.attempts = 0;
                genInputValueChangedTimestampMsecs = 0;

                appContext.isGeneratorWorking = genPowerVal;
            }
        } else {
            genInputValueChangedTimestampMsecs = 0;
        }

        boolean mainsPowerVal = MAINS_POWER.get();

        if (mainsPowerVal != appContext.isMainsPresent) {
            if (mainsInputValveChangedTimestampMsecs == 0) {
                mainsInputValveChangedTimestampMsecs = appContext.getMsecs();
            } else if (appContext.getMsecs() - mainsInputValveChangedTimestampMsecs >= debounceMsecs) {

                appContext.attempts = 0;
                mainsInputValveChangedTimestampMsecs = 0;

                appContext.isMainsPresent = mainsPowerVal;
            }
        } else {
            mainsInputValveChangedTimestampMsecs = 0;
        }
    }

    //checked
    private static void loop() {
        appContext.updateTimestamps();

        checkStatus();
        checkGenerator();

        //no mains, no generator -> start generator
        if (!appContext.isGeneratorWorking && !appContext.isMainsPresent) {
            if (appContext.attempts >= allowedAttempts) {
                // open gas valve
                powerValveFromBattery(true);
                enableValve(true);

                generator.startGenerator();
            } else {
                System.out.println(", Generator start attempts limit reached");
            }
        }

        //generator is working, mains is present -> stop generator
        if (appContext.isGeneratorWorking && appContext.isMainsPresent) {
            generator.stopGenerator();
        }

        //generator is not working, mains is present -> ensure that generator fully stopped
        if (!appContext.isGeneratorWorking && appContext.isMainsPresent) {
            enableValve(false);

            generator.stopStarter();
        }

        //generator is working, valve still powered from batterty -> power valve from generator
        if (appContext.isGeneratorWorking && appContext.isValveOnBattery) {
            powerValveFromBattery(false);
        }

        powerSelect();
    }

    private static void checkGenerator() {
        if (!appContext.isStarterRunning) {
           return;
        }

        if (appContext.isMainsPresent) {
            generator.stopStarter();
            appContext.attempts = 0;
            enableValve(false);
        }

        if (appContext.isGeneratorWorking) {
            System.out.println(", Generator started");
            generator.stopStarter();
            appContext.attempts = 0;
            powerValveFromBattery(false);
        } else if (appContext.getSecs() - starterEnabledTimestampSecs >= starterActiveDelaySecs) {
            System.out.println(", Generator start failed");
            generator.stopStarter();
            appContext.attempts++;

            enableValve(false);
            starterPauseStartedTimestampSecs = appContext.getSecs();
        }
    }

    //checked
    private static void enableValve(boolean isValveShouldBeOpen) {
        appContext.isValveOpen = isValveShouldBeOpen;
        GAS_VALVE_RELAY.set(isValveShouldBeOpen);

        if (!appContext.isValveOpen) {
            powerValveFromBattery(false);
        }
    }

    //checked
    private static void powerValveFromBattery(boolean isValveShouldBePoweredFromBattery) {
        appContext.isValveOnBattery = isValveShouldBePoweredFromBattery;
        GAS_VALVE_POWER_SOURCE_SELECT.set(appContext.isValveOnBattery);
    }

    //checked
    private static void powerSelect() {
        POWER_SOURCE_SELECT_RELAY.set(!appContext.isGeneratorWorking || appContext.isMainsPresent);
    }

}