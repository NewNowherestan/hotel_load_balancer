package dev.stan.autostart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.stan.autostart.OutputPIns.*;
import static dev.stan.autostart.PinState.*;

public class Generator {
    private static final Logger logger = LoggerFactory.getLogger(Generator.class);


    private final AppContext appContext;
    private final Starter starter = new Starter();

    private static final long generatorCooldownDelaySecs = 10;

    private static long generatorCooldownStartedTimestampSecs = 0;

    public Generator(AppContext appContext) {
        this.appContext = appContext;
    }

    // checked
    public void startGenerator() {
        logger.info("Starting generator");


        // if first attempt, let gas flow for a while
        if (appContext.attempts == 0 && !Delay.GAS_PREFLOW.isStarted()) {
            Delay.GAS_PREFLOW.start();
        }

        if (Delay.GAS_PREFLOW.isElapsed()) {
            starter.runStarter();
        } else {
            logger.info("Waiting for gas to flow. Seconds left: " + Delay.GAS_PREFLOW.secondsLeft());
        }
        starter.check();
    }

    // checked
    public void stopGenerator() {
        logger.info("Stopping generator");

        if (generatorCooldownStartedTimestampSecs == 0) {
            generatorCooldownStartedTimestampSecs = appContext.getSecs();
        }

        if (appContext.getSecs() - generatorCooldownStartedTimestampSecs >= generatorCooldownDelaySecs) {
            GAS_VALVE_RELAY.set(LOW);

            appContext.isValveOpen = false;

            generatorCooldownStartedTimestampSecs = 0;
        }
    }

    public void stopStarter() {
        starter.stopStarter();
    }

    public void checkStarter() {
        starter.check();
    }

    class Starter {

        public void check() {
            if (!appContext.isStarterRunning) {
                return;
            }

            if (appContext.isMainsPresent) {
                starter.stopStarter();
                appContext.attempts = 0;
                enableValve(false);
            }

            if (appContext.isGeneratorWorking) {
                logger.info("Generator started");

                starter.stopStarter();
                appContext.attempts = 0;
                powerValveFromBattery(false);
            } else if (Delay.STARTER_ACTIVE_CYCLE.isElapsed()) {
                logger.error("Generator start failed");

                starter.stopStarter();
                appContext.attempts++;

                enableValve(false);
                Delay.STARTER_COOLDOWN.start();

                logger.info("Starting pause before next attempt. Seconds left: " + Delay.STARTER_COOLDOWN.secondsLeft());
            }
        }

        private void runStarter() {
            if (appContext.isGeneratorWorking) {
                logger.info("Generator already started");
                return;
            }

            logger.info("Running starter");

            if (appContext.isStarterRunning) {
                logger.info("Starter started. Seconds to run: " + Delay.STARTER_ACTIVE_CYCLE.secondsLeft());

            } else if (Delay.STARTER_COOLDOWN.isElapsed()) {
                STARTER_RELAY.set(HIGH);

                Delay.STARTER_ACTIVE_CYCLE.start();
                appContext.isStarterRunning = true;

                logger.info("Starter started");

            } else {
                logger.info("Waiting for starter cooldown to end. Seconds left: " + Delay.STARTER_COOLDOWN.secondsLeft());
            }

        }

        private void stopStarter() {
            logger.info("Stopping starter");

            STARTER_RELAY.set(LOW);

            Delay.STARTER_ACTIVE_CYCLE.reset();
            Delay.GAS_PREFLOW.reset();

            appContext.isStarterRunning = false;
        }

    }

    //checked
    public void enableValve(boolean isValveShouldBeOpen) {
        appContext.isValveOpen = isValveShouldBeOpen;
        GAS_VALVE_RELAY.set(isValveShouldBeOpen);

        if (!appContext.isValveOpen) {
            powerValveFromBattery(false);
        }
    }

    //checked
    public void powerValveFromBattery(boolean isValveShouldBePoweredFromBattery) {
        appContext.isValveOnBattery = isValveShouldBePoweredFromBattery;
        GAS_VALVE_POWER_SOURCE_SELECT.set(appContext.isValveOnBattery);
    }
}
