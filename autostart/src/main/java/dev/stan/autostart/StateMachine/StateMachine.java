package dev.stan.autostart.StateMachine;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {
    private State currentState;
    private Map<String, State> states = new HashMap<>();

    public void addState(String name, State state) {
        states.put(name, state);
    }

    public void setState(String name) {
        if (currentState != null) {
            currentState.exit();
        }

        currentState = states.get(name);
        currentState.enter();
    }

    public void handleEvent(String event) {
        if (currentState != null) {
            currentState.handleEvent(event);
        }
    }
}
