package dev.stan.autostart.states;

import dev.stan.autostart.StateMachine.State;
import dev.stan.autostart.StateMachine.StateEventHandler;
import dev.stan.autostart.StateMachine.StateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorMaxRetriesState extends State {
    public ErrorMaxRetriesState(StateMachine stateMachine, State parentState) {
        super(stateMachine, parentState);
    }

    Logger logger = LoggerFactory.getLogger(ErrorMaxRetriesState.class);

    @StateEventHandler("tick")
    private void loop() {
        logger.error("Generator start attempts limit reached. Start attempts: " + appContext.attempts + " Limit: " + appContext.allowedAttempts);

        if (appContext.isMainsPresent) {
            stateMachine.transitionTo(States.GENERATOR_IDLE);
        }
    }
}
