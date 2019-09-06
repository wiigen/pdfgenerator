package com.jw.pdfgenerator.processor;

import org.apache.fop.apps.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class DocumentProcessor implements StreamingOutput {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentProcessor.class);

    private final FopFactory fopFactory;
    private final Source source;
    private final Source stylesheet;

    public DocumentProcessor(FopFactory fopFactory, InputStream xml, InputStream xslt) {
        this.fopFactory = fopFactory;
        this.source = new StreamSource(xml);
        this.stylesheet = new StreamSource(xslt);
    }

    @Override
    public void write(OutputStream outputStream) throws IOException, WebApplicationException {
        try (OutputStream out = new BufferedOutputStream(outputStream)) {
            FOUserAgent userAgent = fopFactory.newFOUserAgent();
            userAgent.getEventBroadcaster().addEventListener(new LogEventListener(LOG));

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, out);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(stylesheet);

            Result result = new SAXResult(fop.getDefaultHandler());
            transformer.transform(source, result);

            LOG.info("Document created");
        } catch (FOPException | TransformerException e) {
            throw new WebApplicationException("FOP processing failed", e);
        }
    }

}
