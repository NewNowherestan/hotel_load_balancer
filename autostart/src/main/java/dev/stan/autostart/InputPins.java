package dev.stan.autostart;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;

public enum InputPins {
        MAINS_POWER(35),
        GENERATOR_POWER(34);

        private final DigitalInput pin;

        InputPins(int address) {
                Context pi4j = AppContext.getContext().getPi4JContext();

                pin = pi4j.create(DigitalInput.newConfigBuilder(pi4j)
                                .id(name())
                                .name(name() + " Pin")
                                .address(address)
                                .build());

        }

        public DigitalInput getPin() {
                return pin;
        }

        public boolean get() {
                return pin.isHigh();
        }
}