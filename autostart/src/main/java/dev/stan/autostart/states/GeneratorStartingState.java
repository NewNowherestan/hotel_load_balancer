package dev.stan.autostart.states;

import dev.stan.autostart.StateMachine.State;
import dev.stan.autostart.StateMachine.StateMachine;

public class GeneratorStartingState extends State {
    public GeneratorStartingState(StateMachine stateMachine, State parentState) {
        super(stateMachine, parentState);
    }
}
