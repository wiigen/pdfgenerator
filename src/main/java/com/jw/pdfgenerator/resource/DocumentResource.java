package com.jw.pdfgenerator.resource;

import com.jw.pdfgenerator.listener.LogEventListener;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Path("document")
public class DocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentResource.class);

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
        StreamingOutput streamingOutput = output -> {
            try (OutputStream out = new BufferedOutputStream(output)) {
                FOUserAgent userAgent = fopFactory.newFOUserAgent();
                userAgent.getEventBroadcaster().addEventListener(new LogEventListener(LOG));

                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, out);

                Source stylesheet = new StreamSource(xslt);
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(stylesheet);

                Source source = new StreamSource(xml);
                Result result = new SAXResult(fop.getDefaultHandler());
                transformer.transform(source, result);

                LOG.info("Document created");
            } catch (Exception e) {
                LOG.error("Error creating document!", e);
                throw new InternalServerErrorException("Error creating document!");
            }
        };

        return Response.ok(streamingOutput)
                .header("Content-Disposition", "attachment; filename=\"document.pdf\"")
                .build();
    }

}
