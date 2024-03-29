package com.jw.pdfgenerator;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.servlet.jakarta.exporter.MetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private final Server server;

    public App(String[] args) {
        String port = args[0];
        LOG.info("Starting server on port {} ...", port);

        // print internal state
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);

        server = new Server(Integer.parseInt(port));
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new ServletContainer(new AppResourceConfig())), "/api/*");
        context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");

        DefaultExports.initialize();
    }

    /**
     * Starts the service
     */
    public void start() throws Exception {
        server.start();
        server.join();
    }

    public static void main(String[] args) throws Exception {
        new App(args).start();
    }
}
