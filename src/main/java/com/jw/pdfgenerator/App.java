package com.jw.pdfgenerator;

import java.io.File;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.jw.pdfgenerator.resource.DocumentResource;

public class App {

    private final Server server;

    public App(String[] args) {
        String port = args[0];
        URI baseUri = UriBuilder.fromUri("http://localhost/")
                .port(Integer.parseInt(port))
                .build();
        ResourceConfig resourceConfig = new ResourceConfig(DocumentResource.class);
        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                FopFactory fopFactory = new FopFactoryBuilder(new File(".").toURI()).build();
                bind(fopFactory).to(FopFactory.class);
            }
        });
        resourceConfig.register(MultiPartFeature.class);
        server = JettyHttpContainerFactory.createServer(baseUri, resourceConfig);
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
