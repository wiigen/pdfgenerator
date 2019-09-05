package com.jw.pdfgenerator.listener;

import org.apache.fop.events.Event;
import org.apache.fop.events.EventFormatter;
import org.apache.fop.events.EventListener;
import org.apache.fop.events.model.EventSeverity;
import org.slf4j.Logger;

public class LogEventListener implements EventListener {

    private final Logger logger;

    public LogEventListener(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void processEvent(Event event) {
        String msg = EventFormatter.format(event);
        EventSeverity severity = event.getSeverity();
        if (severity == EventSeverity.INFO) {
            logger.info(msg);
        } else if (severity == EventSeverity.WARN) {
            logger.warn(msg);
        } else if (severity == EventSeverity.ERROR || severity == EventSeverity.FATAL) {
            if (event.getParam("e") != null) {
                logger.error(msg, (Throwable) event.getParam("e"));
            } else {
                logger.error(msg);
            }
        } else {
            logger.warn("Unknown EventSeverity: " + severity);
        }

    }

}
