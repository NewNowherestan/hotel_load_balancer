package dev.stan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.stan.gui.App;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("-----------------------------------");
        logger.info("Starting application");
        logger.info("Hello, World!");

        App.main(args);
    }
}