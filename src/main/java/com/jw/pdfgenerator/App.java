package com.jw.pdfgenerator;

import com.jw.pdfgenerator.servlet.DocumentServlet;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.MultipartConfigElement;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private static final long DEFAULT_MAX_FILE_SIZE     = 1024 * 1024 * 5;
    private static final long DEFAULT_MAX_REQUEST_SIZE  = 1024 * 1024 * 5 * 5;
    private static final int DEFAULT_MAX_SIZE_THRESHOLD = 1024 * 1024;

    private final Server server;

    public App(String[] args) {
        String port = args[0];
        LOG.info("Starting server on port {} ...", port);

        server = new Server(Integer.parseInt(port));
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

        ServletHolder documentServletHolder = createdocumentServletHolder();
        context.addServlet(documentServletHolder, "/api/documents");

        context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");

        DefaultExports.initialize();
    }

    /**
     * Starts the service
     */
    public App start() throws Exception {
        server.start();
        return this;
    }

    public void join() throws InterruptedException {
        server.join();
    }

    /**
     * Stop the service
     */
    public void stop() throws Exception {
        server.stop();
    }

    public static void main(String[] args) throws Exception {
        new App(args).start().join();
    }

    private ServletHolder createdocumentServletHolder() {

        LOG.info("Setting Multipart max file size to {} and max request size {}",
                DEFAULT_MAX_FILE_SIZE, DEFAULT_MAX_REQUEST_SIZE);

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                "",
                DEFAULT_MAX_FILE_SIZE,
                DEFAULT_MAX_REQUEST_SIZE,
                DEFAULT_MAX_SIZE_THRESHOLD);

        ServletHolder holder = new ServletHolder(new DocumentServlet());
        holder.getRegistration().setMultipartConfig(multipartConfigElement);

        return holder;
    }

}
