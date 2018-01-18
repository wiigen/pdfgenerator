package com.jw.pdfgenerator.resource;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.jw.pdfgenerator.resource.DocumentResource;

public class DocumentResourceTest extends JerseyTest {

    private String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
            "<note>\n" + 
            "  <to>Tove</to>\n" + 
            "  <from>Jani</from>\n" + 
            "  <heading>Reminder</heading>\n" + 
            "  <body>Don't forget me this weekend!</body>\n" + 
            "</note>";

    @Override
    protected Application configure() {
        ResourceConfig resourceConfig = new ResourceConfig(DocumentResource.class);
        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                FopFactory fopFactory = new FopFactoryBuilder(new File(".").toURI())
                        .build();
                bind(fopFactory).to(FopFactory.class);
            }
        });
        resourceConfig.register(MultiPartFeature.class);
        return resourceConfig;
    }

    @Test
    public void shouldCreateDocumentWith200Ok() {
        String xslt = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
                "<xsl:stylesheet version=\"1.0\"\n" + 
                "      xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n" + 
                "      xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n" + 
                "  <xsl:output method=\"xml\" indent=\"yes\"/>\n" + 
                "  <xsl:template match=\"/\">\n" + 
                "    <fo:root>\n" + 
                "      <fo:layout-master-set>\n" + 
                "        <fo:simple-page-master master-name=\"A4-portrait\"\n" + 
                "              page-height=\"29.7cm\" page-width=\"21.0cm\" margin=\"2cm\">\n" + 
                "          <fo:region-body/>\n" + 
                "        </fo:simple-page-master>\n" + 
                "      </fo:layout-master-set>\n" + 
                "      <fo:page-sequence master-reference=\"A4-portrait\">\n" + 
                "        <fo:flow flow-name=\"xsl-region-body\">\n" + 
                "          <fo:block>\n" + 
                "            Hello, <xsl:value-of select=\"name\"/>!\n" + 
                "          </fo:block>\n" + 
                "        </fo:flow>\n" + 
                "      </fo:page-sequence>\n" + 
                "    </fo:root>\n" + 
                "  </xsl:template>\n" + 
                "</xsl:stylesheet>";
        FormDataMultiPart form = new FormDataMultiPart();
        form.bodyPart(new StreamDataBodyPart("xml", new ByteArrayInputStream(xml.getBytes())));
        form.bodyPart(new StreamDataBodyPart("xslt", new ByteArrayInputStream(xslt.getBytes())));

        Response response = target("document")
                .register(MultiPartFeature.class)
                .request()
                .post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));

        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldReturn500ErrorWhenUnexpectedExceptionOccurs() {
        String malformedXslt = "<foo>";
        FormDataMultiPart form = new FormDataMultiPart();
        form.bodyPart(new StreamDataBodyPart("xml", new ByteArrayInputStream(xml.getBytes())));
        form.bodyPart(new StreamDataBodyPart("xslt", new ByteArrayInputStream(malformedXslt.getBytes())));

        Response response = target("document")
                .register(MultiPartFeature.class)
                .request()
                .post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));

        assertEquals(500, response.getStatus());
    }
}
