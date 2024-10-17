package dev.stan.autostart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.stan.autostart.InputPins.*;
import static dev.stan.autostart.OutputPIns.*;

public class Autostart {

    private static final Logger logger = LoggerFactory.getLogger(Autostart.class);

    //outputs - indicators
    // private static DigitalOutput genPowerIndicatorPin;
    // private static DigitalOutput mainsPowerIndicatorPin;

    //constants
    private static final int debounceMsecs = 150;
    private static final int allowedAttempts = 4;

    private static long mainsInputValveChangedTimestampMsecs = 0;
    private static long genInputValueChangedTimestampMsecs = 0;




    private final static AppContext appContext = AppContext.getContext();
    private final static Generator generator = new Generator(appContext);
    private static Thread thread;

    public static void main(String[] args) {
        start();
    }

    public static AppContext start() {

        if (thread == null) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

            Runnable task = () -> {
                // Main loop
                while (true) {
                    loop();
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            thread = new Thread(task);
            thread.start();
        }

        return appContext;
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
        generator.checkStarter();

        //no mains, no generator -> start generator
        if (!appContext.isGeneratorWorking && !appContext.isMainsPresent) {
            if (appContext.attempts < allowedAttempts) {
                // open gas valve
                generator.powerValveFromBattery(true);
                generator.enableValve(true);

                generator.startGenerator();
            } else {
                logger.error("Generator start attempts limit reached. Start attempts: " + appContext.attempts + " Limit: " + allowedAttempts);
            }
        }

        //generator is working, mains is present -> stop generator
        if (appContext.isGeneratorWorking && appContext.isMainsPresent) {
            generator.stopGenerator();
        }

        //generator is not working, mains is present -> ensure that generator fully stopped
        if (!appContext.isGeneratorWorking && appContext.isMainsPresent) {
            generator.enableValve(false);

            generator.stopStarter();
        }

        //generator is working, valve still powered from batterty -> power valve from generator
        if (appContext.isGeneratorWorking && appContext.isValveOnBattery) {
            generator.powerValveFromBattery(false);
        }

        powerSelect();
    }



    //checked
    private static void powerSelect() {
        POWER_SOURCE_SELECT_RELAY.set(!appContext.isGeneratorWorking || appContext.isMainsPresent);
    }

}