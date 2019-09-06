package com.jw.pdfgenerator.resource;

import com.jw.pdfgenerator.processor.DocumentProcessor;
import org.apache.fop.apps.FopFactory;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;

@Path("document")
public class DocumentResource {

    private final FopFactory fopFactory;

    @Inject
    public DocumentResource(FopFactory fopFactory) {
        this.fopFactory = fopFactory;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response createDocument(
            @FormDataParam("xml") InputStream xml,
            @FormDataParam("xslt") InputStream xslt)
    {
        StreamingOutput streamingOutput = new DocumentProcessor(fopFactory, xml, xslt);

        return Response.ok(streamingOutput)
                .header("Content-Disposition", "attachment; filename=\"document.pdf\"")
                .build();
    }

}
