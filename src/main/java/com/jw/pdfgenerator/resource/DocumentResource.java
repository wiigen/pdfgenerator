package com.jw.pdfgenerator.resource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import java.io.IOException;
import java.io.InputStream;

@Path("document")
public class DocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentResource.class);

    @Inject
    private FopFactory fopFactory;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response createDocument(
            @FormDataParam("xml") InputStream xml,
            @FormDataParam("xslt") InputStream xslt)
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document xmlDocument = validateInput(dbf, xml);
        Document xsltDocument = validateInput(dbf, xslt);

        StreamingOutput streamingOutput = output -> {
            try {
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, output);

                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(new DOMSource(xsltDocument));

                Result result = new SAXResult(fop.getDefaultHandler());
                transformer.transform(new DOMSource(xmlDocument), result);
            } catch (Exception e) {
                LOG.error("Error creating document!", e);
                throw new InternalServerErrorException("Error creating document!");
            }
        };
        LOG.info("Document created");
        return Response.ok(streamingOutput)
                .header("Content-Disposition", "attachment; filename=\"document.pdf\"")
                .build();
    }

    private static Document validateInput(DocumentBuilderFactory dbf, InputStream is) {
        try {
            return dbf.newDocumentBuilder().parse(is);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new BadRequestException("Malformed XML", e);
        }
    }

}
