package dev.stan.autostart;

import com.pi4j.io.gpio.digital.DigitalState;

public enum PinState {
    LOW(DigitalState.LOW),
    HIGH(DigitalState.HIGH);

    private final DigitalState state;

    PinState(DigitalState state) {
        this.state = state;
    }

    DigitalState getValue() {
        return state;
    }
    
}
