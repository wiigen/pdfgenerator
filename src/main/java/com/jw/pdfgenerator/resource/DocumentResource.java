package com.jw.pdfgenerator.resource;

import com.jw.pdfgenerator.processor.DocumentProcessor;
import io.prometheus.client.Histogram;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.apache.fop.apps.FopFactory;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;

@Path("documents")
public class DocumentResource {

    static final Histogram requestLatency = Histogram.build()
            .name("requests_latency_seconds")
            .help("Request latency in seconds.")
            .register();

    private final FopFactory fopFactory;

    @Inject
    public DocumentResource(FopFactory fopFactory) {
        this.fopFactory = fopFactory;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "Create document", description = "Create document based on XML and XSLT")
    public Response createDocument(
            @FormDataParam("xml") InputStream xml,
            @FormDataParam("xslt") InputStream xslt)
    {
        try (Histogram.Timer ignored = requestLatency.startTimer()) {
            StreamingOutput streamingOutput = new DocumentProcessor(fopFactory, xml, xslt);

            return Response.ok(streamingOutput)
                    .header("Content-Disposition", "attachment; filename=\"document.pdf\"")
                    .build();
        }
    }

}
