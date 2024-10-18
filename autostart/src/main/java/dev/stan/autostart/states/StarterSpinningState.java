package dev.stan.autostart.states;

import dev.stan.autostart.StateMachine.State;
import dev.stan.autostart.StateMachine.StateMachine;

public class StarterSpinningState extends State {

    public StarterSpinningState(StateMachine stateMachine, State parentState) {
        super(stateMachine, parentState);
    }
}
