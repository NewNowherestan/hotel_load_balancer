package dev.stan.autostart.states;

import dev.stan.autostart.StateMachine.State;
import dev.stan.autostart.StateMachine.StateMachine;

public class StarterGasPreflowState extends State {
    public StarterGasPreflowState(StateMachine stateMachine, State parentState) {
        super(stateMachine, parentState);
    }
}
