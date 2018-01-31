package com.jw.pdfgenerator;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;

public class App {

    private final Server server;

    public App(String[] args) {
        String port = args[0];
        URI baseUri = UriBuilder.fromUri("http://localhost/")
                .port(Integer.parseInt(port))
                .build();
        server = JettyHttpContainerFactory.createServer(baseUri, new AppResourceConfig());
    }

    /**
     * Starts the service
     * 
     * @throws Exception if the server fails to start
     */
    public void start() throws Exception {
        server.start();
        server.join();
    }

    public static void main(String[] args) throws Exception {
        new App(args).start();
    }
}
