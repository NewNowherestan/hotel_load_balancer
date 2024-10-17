package dev.stan.autostart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.stan.autostart.OutputPIns.*;
import static dev.stan.autostart.PinState.*;

public class Generator {
    private static final Logger logger = LoggerFactory.getLogger(Generator.class);

    private static final long generatorCooldownDelaySecs = 10;

    private final AppContext appContext;
    private final Starter starter = new Starter();

    private static final long gasValveOpenDelaySecs = 5;
    private static long generatorCooldownStartedTimestampSecs = 0;
    private static long gasValveOpenOnStarttimestampSecs = 0;

    private final long starterPauseDelaySecs = 20;
    private static final long starterActiveDelaySecs = 10;

    private long starterPauseStartedTimestampSecs = 0;
    private static long starterEnabledTimestampSecs = 0;

    public Generator(AppContext appContext) {
        this.appContext = appContext;
    }

    // checked
    public void startGenerator() {
        logger.info("Starting generator");


        // if first attempt, let gas flow for a while
        if (appContext.attempts == 0 && gasValveOpenOnStarttimestampSecs == 0) {
            gasValveOpenOnStarttimestampSecs = appContext.getSecs();
        }

        if (appContext.getSecs() - gasValveOpenOnStarttimestampSecs >= gasValveOpenDelaySecs) {
            starter.runStarter();
        } else {
            logger.info("Waiting for gas to flow. Seconds left: " + (gasValveOpenDelaySecs - (appContext.getSecs() - gasValveOpenOnStarttimestampSecs)));
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
            } else if (appContext.getSecs() - starterEnabledTimestampSecs >= starterActiveDelaySecs) {
                logger.error("Generator start failed");

                starter.stopStarter();
                appContext.attempts++;

                enableValve(false);
                starterPauseStartedTimestampSecs = appContext.getSecs();

                logger.info("Starting pause before next attempt. Seconds left: " + starterPauseDelaySecs);
            }
        }

        private void runStarter() {
            if (appContext.isGeneratorWorking) {
                logger.info("Generator already started");
                return;
            }

            logger.info("Running starter");

            if (appContext.isStarterRunning) {
                logger.info("Starter started. Seconds to run: " + (starterActiveDelaySecs - (appContext.getSecs() - starterEnabledTimestampSecs)));

            } else if (appContext.getSecs() - starterPauseStartedTimestampSecs >= starterPauseDelaySecs) {
                STARTER_RELAY.set(HIGH);

                starterEnabledTimestampSecs = appContext.getSecs();
                appContext.isStarterRunning = true;

                logger.info("Starter started");

            } else {
                logger.info("Waiting for starter cooldown to end. Seconds left: " + (starterPauseDelaySecs - (appContext.getSecs() - starterPauseStartedTimestampSecs)));
            }

        }

        private void stopStarter() {
            logger.info("Stopping starter");

            STARTER_RELAY.set(LOW);

            starterEnabledTimestampSecs = 0;
            gasValveOpenOnStarttimestampSecs = 0;

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
