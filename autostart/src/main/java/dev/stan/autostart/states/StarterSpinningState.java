package dev.stan.autostart.states;

import dev.stan.autostart.StateMachine.State;
import dev.stan.autostart.StateMachine.StateEventHandler;
import dev.stan.autostart.StateMachine.StateMachine;

public class StarterSpinningState extends State {

    public StarterSpinningState(StateMachine stateMachine, State parentState) {
        super(stateMachine, parentState);
    }

    @StateEventHandler("tick")
    private void loop() {
        if (isGeneratorStarted()) {
            stateMachine.transitionTo(States.GENERATOR_RUNNING);
        } else if (!spinStarter()) {
            stateMachine.transitionTo(States.GENERATOR_STARTING);
        }
    }

    private boolean spinStarter() {
        return false;
    }

    private boolean isGeneratorStarted() {
        return false;
    }
}
