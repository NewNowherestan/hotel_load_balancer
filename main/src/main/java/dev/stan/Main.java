package dev.stan;

import dev.stan.autostart.AppContext;
import dev.stan.logging.LambdaAppender;
import dev.stan.gui.Gui;
import dev.stan.autostart.Autostart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("-----------------------------------");
        logger.info("Starting application");
        logger.info("Hello, World!");

        logger.info("Registering consumer");
        LambdaAppender.registerConsumer("gui", Gui::printLog);

        Gui.start();

        sleep(2000);

        AppContext context = Autostart.start();

        sleep(2000);


        Gui.updateSysParams(context.getSysParams());
        Gui.plotIO(context.getIO());


        logger.info("Application Started");
        logger.info("Hello, World!");
    }
}