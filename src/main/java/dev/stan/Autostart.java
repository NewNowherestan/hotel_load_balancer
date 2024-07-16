package dev.stan;
import com.pi4j.Pi4J;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;

import static dev.stan.OutputPIns.*;
import static dev.stan.PinState.*;
import static dev.stan.InputPins.*;

public class Autostart {

    //outputs - indicators
    // private static DigitalOutput genPowerIndicatorPin;
    // private static DigitalOutput mainsPowerIndicatorPin;

    //constants
    private static final long gasValveOpenDelaySecs = 5; 
    private static final long starterActiveDelaySecs = 10; 
    private static final long starterPauseDelaySecs = 20; 
    private static final long generatorCooldownDelaySecs = 10;
    private static final int allowedAttempts = 4;
    private static final int debounceMsecs = 150;

    //state
    private static boolean isStarterRunning = false;
    private static boolean isGeneratorWorking = false;
    private static boolean isMainsPresent = false;
    private static boolean isValveOpen = false;


    private static int attempts = 0;

    private static long secs = 0;
    private static long msecs = 0;

    private static long starterEnabledTimestampSecs = 0;
    private static long generatorCooldownStartedTimestampSecs = 0;
    private static long starterPauseStartedTimestampSecs = 0;
    private static long gasValveOpenOnStarttimestampSecs = 0;

    private static long mainsInputValveChangedTimestampMsecs = 0;
    private static long genInputValueChangedTimestampMsecs = 0;



    private static boolean isValveOnBattery = false;


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

        if (genPowerVal != isGeneratorWorking) {
            
            if (genInputValueChangedTimestampMsecs == 0) {
                genInputValueChangedTimestampMsecs = msecs;
            } else if (msecs - genInputValueChangedTimestampMsecs >= debounceMsecs) {

                attempts = 0;
                genInputValueChangedTimestampMsecs = 0;

                isGeneratorWorking = genPowerVal;
            }
        } else {
            genInputValueChangedTimestampMsecs = 0;
        }

        boolean mainsPowerVal = MAINS_POWER.get();

        if (mainsPowerVal != isMainsPresent) {
            if (mainsInputValveChangedTimestampMsecs == 0) {
                mainsInputValveChangedTimestampMsecs = msecs;
            } else if (msecs - mainsInputValveChangedTimestampMsecs >= debounceMsecs) {

                attempts = 0;
                mainsInputValveChangedTimestampMsecs = 0;

                isMainsPresent = mainsPowerVal;
            }
        } else {
            mainsInputValveChangedTimestampMsecs = 0;
        }
    }

    //checked
    private static void loop() {
        msecs = System.currentTimeMillis();
        secs = msecs / 1000;

        checkStatus();
        checkStarter();

        //no mains, no generator -> start generator
        if (!isGeneratorWorking && !isMainsPresent) {
            startGenerator();
        }

        //generator is working, mains is present -> stop generator
        if (isGeneratorWorking && isMainsPresent) {
            stopGenerator();
        }

        //generator is not working, mains is present -> ensure that generator fully stopped
        if (!isGeneratorWorking && isMainsPresent) {
            enableValve(false);

            stopStarter();
        }

        //generator is working, valve still powered from batterty -> power valve from generator
        if (isGeneratorWorking && isValveOnBattery) {
            powerValveFromBattery(false);
        }

        powerSelect();
    }

    //checked
    private static void enableValve(boolean isValveShouldBeOpen) {
        isValveOpen = isValveShouldBeOpen;
        GAS_VALVE_RELAY.set(isValveShouldBeOpen);

        if (!isValveOpen) {
            powerValveFromBattery(false);
        }
    }

    //checked
    private static void powerValveFromBattery(boolean isValveShouldBePoweredFromBattery) {
        isValveOnBattery = isValveShouldBePoweredFromBattery;
        GAS_VALVE_POWER_SOURCE_SELECT.set(isValveOnBattery);
    }

    //checked
    private static void powerSelect() {
        POWER_SOURCE_SELECT_RELAY.set(!isGeneratorWorking || isMainsPresent);
    }

    //checked
    private static void startGenerator() {
        System.out.println(", Start generator");

        if (attempts >= allowedAttempts) {
            System.out.println(", Generator start attempts limit reached");
            return;
        }

        //open gas valve
        powerValveFromBattery(true);
        enableValve(true);

        //if first attempt, let gas flow for a while
        if (attempts == 0) {
            gasValveOpenOnStarttimestampSecs = secs;
        }

        if (secs - gasValveOpenOnStarttimestampSecs >= gasValveOpenDelaySecs) {
            runStarter();
        }
    }

    //checked   
    private static void stopGenerator() {
        System.out.println(", Stop generator");

        if (generatorCooldownStartedTimestampSecs == 0) {
            generatorCooldownStartedTimestampSecs = secs;
        }

        if (secs - generatorCooldownStartedTimestampSecs >= generatorCooldownDelaySecs) {
            GAS_VALVE_RELAY.set(LOW);

            isValveOpen = false;

            generatorCooldownStartedTimestampSecs = 0;
        }
    }
    //checked
    private static void runStarter() {
        if (isGeneratorWorking) return;

        System.out.println(", Run starter");

        if (!isStarterRunning && secs - starterPauseStartedTimestampSecs >= starterPauseDelaySecs) {
            STARTER_RELAY.set(HIGH);

            starterEnabledTimestampSecs = secs;
            isStarterRunning = true;
        }
    }

    //checked
    private static void stopStarter() {
        System.out.println(", Stop starter");

        STARTER_RELAY.set(LOW);

        starterEnabledTimestampSecs = 0;
        gasValveOpenOnStarttimestampSecs = 0;

        isStarterRunning = false;
    }

    //checked
    private static void checkStarter() {
        if (!isStarterRunning) {
            return;
        }

        long secs = System.currentTimeMillis() / 1000;

        if (isMainsPresent) {
            stopStarter();
            attempts = 0;
            enableValve(false);
        }

        if (isGeneratorWorking) {
            System.out.println(", Generator started");
            stopStarter();
            attempts = 0;
            powerValveFromBattery(false);
        } else if (secs - starterEnabledTimestampSecs >= starterActiveDelaySecs) {
            System.out.println(", Generator start failed");
            stopStarter();
            attempts++;

            enableValve(false);
            starterPauseStartedTimestampSecs = secs;
        }
    }
}