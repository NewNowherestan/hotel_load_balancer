package dev.stan.autostart.states;

import dev.stan.autostart.StateMachine.State;
import dev.stan.autostart.StateMachine.StateEventHandler;
import dev.stan.autostart.StateMachine.StateMachine;

public class GeneratorIdleState extends State {

    public GeneratorIdleState(StateMachine stateMachine, State parentState) {
        super(stateMachine, parentState);
    }

    @StateEventHandler("tick")
    private void loop() {
        if (!appContext.isGeneratorWorking && !appContext.isMainsPresent) {
            stateMachine.transitionTo(States.GENERATOR_STARTING);
        }
    }

};
