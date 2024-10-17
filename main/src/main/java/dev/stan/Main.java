package dev.stan;

import dev.stan.autostart.AppContext;
import dev.stan.logging.LambdaAppender;
import dev.stan.gui.Gui;
import dev.stan.autostart.Autostart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("-----------------------------------");
        logger.info("Starting application");
        logger.info("Registering consumer");
        LambdaAppender.registerConsumer("gui", Gui::printLog);

        Gui.start();
        sleep(300);
        AppContext context = Autostart.start();
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> {
                    Gui.updateSysParams(context.getSysParams());
                    Gui.plotIO(context.getIO());
                }, 0, 1, TimeUnit.SECONDS);


        Thread currentThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            currentThread.interrupt();
            logger.info("Application terminated by user.");
        }));

        logger.info("Application Started");
        logger.info("Hello, World!");
    }
}