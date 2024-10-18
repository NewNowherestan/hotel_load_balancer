package dev.stan.autostart.StateMachine;

import dev.stan.autostart.states.States;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {
    private State currentState;
    private Map<States, State> states = new HashMap<>();

    public void addState(States name, State state) {
        states.put(name, state);
    }

    public void transitionTo(States name) {
        if (currentState != null) {
            currentState.exit();
        }

        currentState = states.get(name);
        currentState.enter();
    }

    public void tick() {
        handleEvent("tick");
    }

    public void handleEvent(String event) {
        if (currentState != null) {
            currentState.handleEvent(event);
        }
    }
}
