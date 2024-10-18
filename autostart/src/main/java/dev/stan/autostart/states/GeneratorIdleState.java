package dev.stan.autostart.states;

import dev.stan.autostart.StateMachine.State;
import dev.stan.autostart.StateMachine.StateMachine;

public class GeneratorIdleState extends State {

    public GeneratorIdleState(StateMachine stateMachine, State parentState) {
        super(stateMachine, parentState);
    }

};
