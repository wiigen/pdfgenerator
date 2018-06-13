package com.jw.pdfgenerator.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("document")
public class DocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentResource.class);

    @Inject
    private FopFactory fopFactory;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response createDocument(
            @FormDataParam("xml") final InputStream xml,
            @FormDataParam("xslt") final InputStream xslt)
    {
        StreamingOutput streamingOutput = new StreamingOutput() {
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, output);

                    TransformerFactory factory = TransformerFactory.newInstance();
                    Transformer transformer = factory.newTransformer(new StreamSource(xslt));

                    Result result = new SAXResult(fop.getDefaultHandler());
                    transformer.transform(new StreamSource(xml), result);
                } catch (Exception e) {
                    LOG.error("Error creating document!", e);
                    throw new InternalServerErrorException("Error creating document!");
                }
            }
        };
        LOG.info("Document created");
        return Response.ok(streamingOutput)
                .header("Content-Disposition", "attachment; filename=\"document.pdf\"")
                .build();
    }

}
