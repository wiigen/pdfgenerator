package com.jw.pdfgenerator;

import com.jw.pdfgenerator.resource.DocumentResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.File;

public class AppResourceConfig extends ResourceConfig {
    public AppResourceConfig() {
        super(DocumentResource.class);

        register(MultiPartFeature.class);

        OpenApiResource openApiResource = new OpenApiResource();
        register(openApiResource);

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                FopFactory fopFactory = new FopFactoryBuilder(new File(".").toURI()).build();
                bind(fopFactory).to(FopFactory.class);
            }
        });
    }
}
