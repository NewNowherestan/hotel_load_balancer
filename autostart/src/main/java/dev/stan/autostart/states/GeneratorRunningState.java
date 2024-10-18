package dev.stan.autostart.states;

import dev.stan.autostart.StateMachine.State;
import dev.stan.autostart.StateMachine.StateMachine;

public class GeneratorRunningState extends State {
    public GeneratorRunningState(StateMachine stateMachine, State parentState) {
        super(stateMachine, parentState);
    }
}
