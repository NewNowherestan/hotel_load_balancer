package dev.stan.autostart.states;

import dev.stan.autostart.StateMachine.State;
import dev.stan.autostart.StateMachine.StateMachine;

public class GeneratorStoppingState extends State {
    public GeneratorStoppingState(StateMachine stateMachine, State parentState) {
        super(stateMachine, parentState);
    }
}
