package com.jw.pdfgenerator.servlet;

import io.prometheus.client.Histogram;
import org.apache.fop.apps.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class DocumentServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentServlet.class);

    static final Histogram requestLatency = Histogram.build()
            .name("requests_latency_seconds")
            .help("Request latency in seconds.")
            .register();

    private FopFactory fopFactory;
    private TransformerFactory tFactory;

    @Override
    public void init() {
        this.fopFactory = new FopFactoryBuilder(new File(".").toURI()).build();
        this.tFactory = TransformerFactory.newInstance();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        Histogram.Timer requestTimer = requestLatency.startTimer();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            //Setup input
            Source source = new StreamSource(req.getPart("xml").getInputStream());
            Source stylesheet = new StreamSource(req.getPart("xslt").getInputStream());

            //Setup FOP
            FOUserAgent userAgent = fopFactory.newFOUserAgent();
            userAgent.getEventBroadcaster().addEventListener(new LogEventListener(LOG));
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, out);

            //Setup Transformer
            Transformer transformer = tFactory.newTransformer(stylesheet);

            //Make sure the XSL transformation's result is piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            //Start the transformation and rendering process
            transformer.transform(source, res);

            //Prepare response
            resp.setContentType("application/pdf");
            resp.setContentLength(out.size());

            //Send content to Browser
            resp.getOutputStream().write(out.toByteArray());
            resp.getOutputStream().flush();
        } catch (Exception e) {
            throw new ServletException("FOP processing failed", e);
        } finally {
            requestTimer.observeDuration();
        }
    }
}
