package dev.stan.autostart.states;

import dev.stan.autostart.StateMachine.State;
import dev.stan.autostart.StateMachine.StateEventHandler;
import dev.stan.autostart.StateMachine.StateMachine;

public class GeneratorStartingState extends State {
    public GeneratorStartingState(StateMachine stateMachine, State parentState) {
        super(stateMachine, parentState);
    }

    @StateEventHandler("tick")
    private void loop() {
        if (appContext.attempts < allowedAttempts) {
            // open gas valve
            if (appContext.attempts == 0) {
                generator.powerValveFromBattery(false);
                generator.enableValve(true);
            } else {
                stateMachine.transitionTo(States.STARTER_COOLDOWN);
            }

            generator.startGenerator();
            stateMachine.transitionTo(States.STARTER_SPINNING);
        } else {
            stateMachine.transitionTo(States.ERROR_MAX_RETRIES);
        }
    }
}
