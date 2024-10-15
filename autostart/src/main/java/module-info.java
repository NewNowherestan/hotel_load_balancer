module dev.stan.autostart {
    requires transitive com.pi4j;
    requires com.pi4j.plugin.mock;
    requires com.pi4j.plugin.pigpio;
    requires com.pi4j.plugin.raspberrypi;

    requires org.slf4j;

    exports dev.stan.autostart;
    
}
