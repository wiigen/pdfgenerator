package com.jw.pdfgenerator.resource;

import com.jw.pdfgenerator.AppResourceConfig;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

public class DocumentResourceTest extends JerseyTest {

    private final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<note>\n"
            + "  <to>Tove</to>\n"
            + "  <from>Jani</from>\n"
            + "  <heading>Reminder</heading>\n"
            + "  <body>Don't forget me this weekend!</body>\n"
            + "</note>";

    @Override
    protected Application configure() {
        return new AppResourceConfig();
    }

    @Test
    public void shouldCreateDocumentWith200Ok() {
        String xslt = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<xsl:stylesheet version=\"1.0\"\n"
                + "      xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n"
                + "      xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n"
                + "  <xsl:output method=\"xml\" indent=\"yes\"/>\n"
                + "  <xsl:template match=\"/\">\n"
                + "    <fo:root>\n"
                + "      <fo:layout-master-set>\n"
                + "        <fo:simple-page-master master-name=\"A4-portrait\"\n"
                + "              page-height=\"29.7cm\" page-width=\"21.0cm\" margin=\"2cm\">\n"
                + "          <fo:region-body/>\n"
                + "        </fo:simple-page-master>\n"
                + "      </fo:layout-master-set>\n"
                + "      <fo:page-sequence master-reference=\"A4-portrait\">\n"
                + "        <fo:flow flow-name=\"xsl-region-body\">\n"
                + "          <fo:block>\n"
                + "            Hello, <xsl:value-of select=\"note/to\"/>!\n"
                + "          </fo:block>\n"
                + "        </fo:flow>\n"
                + "      </fo:page-sequence>\n"
                + "    </fo:root>\n"
                + "  </xsl:template>\n"
                + "</xsl:stylesheet>";

        FormDataMultiPart form = new FormDataMultiPart();
        form.bodyPart(new StreamDataBodyPart("xml", new ByteArrayInputStream(xml.getBytes())));
        form.bodyPart(new StreamDataBodyPart("xslt", new ByteArrayInputStream(xslt.getBytes())));

        Response response = target("document")
                .register(MultiPartFeature.class)
                .request()
                .post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));

        assertEquals(200, response.getStatus());
    }

}
