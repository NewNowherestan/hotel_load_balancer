package dev.stan.autostart.states;

import dev.stan.autostart.StateMachine.State;
import dev.stan.autostart.StateMachine.StateMachine;

public class GeneratorMaxStartingAttemptsFailedState extends State {
    public GeneratorMaxStartingAttemptsFailedState(StateMachine stateMachine, State parentState) {
        super(stateMachine, parentState);
    }
}
