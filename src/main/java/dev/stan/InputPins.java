package dev.stan;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;

public enum InputPins {
        MAINS_POWER(35),
        GENERATOR_POWER(34);

        private final DigitalInput pin;

        InputPins(int address) {
                Context pi4j = Pi4JContext.getContext();

                pin = pi4j.create(DigitalInput.newConfigBuilder(pi4j)
                                .id(name())
                                .name(name() + " Pin")
                                .address(address)
                                .provider("pigpio-digital-input"));

        }

        public DigitalInput getPin() {
                return pin;
        }

        public boolean get() {
                return pin.isHigh();
        }
}