package dev.stan.autostart.states;

import dev.stan.autostart.StateMachine.State;
import dev.stan.autostart.StateMachine.StateMachine;

public class StarterCooldownState extends State {
    public StarterCooldownState(StateMachine stateMachine, State parentState) {
        super(stateMachine, parentState);
    }
}
