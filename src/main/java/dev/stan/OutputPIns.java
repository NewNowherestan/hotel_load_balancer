package dev.stan;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import static dev.stan.PinState.*;

public enum OutputPIns {

    POWER_SOURCE_SELECT_RELAY(27, LOW),
    GAS_VALVE_RELAY(36, LOW),
    GAS_VALVE_POWER_SOURCE_SELECT(25, LOW),
    STARTER_RELAY(33, LOW);

    private final DigitalOutput pin;

    // implement lazy initialization
    OutputPIns(int address, PinState state) {
        Context pi4j = Pi4JContext.getContext();

        pin = pi4j.create(DigitalOutput.newConfigBuilder(pi4j)
                .id(name())
                .name(name() + " Pin")
                .address(address)
                .initial(state.getValue())
                .shutdown(state.getValue())
                .provider("pigpio-digital-output"));

    }

    public DigitalOutput getPin() {
        return pin;
    }

    public void set(PinState state) {
        pin.state(state.getValue());
    }

    public void set(boolean state) {
        pin.setState(state);
    }

}
