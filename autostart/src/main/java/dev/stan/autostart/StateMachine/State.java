package dev.stan.autostart.StateMachine;

import java.lang.reflect.Method;
import java.util.Map;

public abstract class State {
    protected StateMachine stateMachine;
    private Map<String, Method> entryHandlers;
    protected State parentState;

    public State(StateMachine stateMachine, State parentState) {
        this.stateMachine = stateMachine;
        this.parentState = parentState;
        initEventHandlers();
    }

    public State getParentState() {
        return parentState;
    }

    private void initEventHandlers() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(StateEventHandler.class)) {
                StateEventHandler annotation = method.getAnnotation(StateEventHandler.class);
                entryHandlers.put(annotation.value(), method);
            }
        }
    }

    public boolean handleEvent(String event) {
        Method handler = entryHandlers.get(event);
        if (handler != null) {
            try {
                handler.invoke(this);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            if (parentState != null) {
                return parentState.handleEvent(event);
            }
        }
        return false;
    }

    public void enter() {}

    public void exit() {
        if (parentState != null) {
            parentState.exit();
        }
    }

}
