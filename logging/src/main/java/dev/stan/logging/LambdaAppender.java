package dev.stan.logging;

import java.io.Serializable;
// In the logger module
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Plugin(name = "LambdaAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class LambdaAppender extends AbstractAppender {
    private static final ConcurrentHashMap<String, Consumer<String>> consumers = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(LambdaAppender.class);

    protected LambdaAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions,
                             Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    public static void registerConsumer(String name, Consumer<String> consumer) {
        if (consumers.putIfAbsent(name, consumer) != null) {
            throw new IllegalArgumentException("A consumer with the name '" + name + "' is already registered.");
        }
    }

    public static void removeConsumer(String name) {
        consumers.remove(name);
    }

    @Override
    public void append(LogEvent event) {
        String message = new String(getLayout().toByteArray(event));
        consumers.values().forEach(consumer -> consumer.accept(message));
    }

    @PluginFactory
    public static LambdaAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
            @PluginElement("Properties") Property[] properties) {

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new LambdaAppender(name, filter, layout, ignoreExceptions, properties);
    }
}
