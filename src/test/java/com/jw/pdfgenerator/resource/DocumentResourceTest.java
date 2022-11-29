package com.jw.pdfgenerator.resource;

import com.jw.pdfgenerator.AppResourceConfig;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class DocumentResourceTest extends JerseyTest {

    private final String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <note>
              <to>Tove</to>
              <from>Jani</from>
              <heading>Reminder</heading>
              <body>Don't forget me this weekend!</body>
            </note>""";

    @Override
    protected Application configure() {
        return new AppResourceConfig();
    }

    @Test
    public void shouldCreateDocumentWith200Ok() throws Exception {
        String xslt = """
                <?xml version="1.0" encoding="utf-8"?>
                <xsl:stylesheet version="1.0"
                      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                      xmlns:fo="http://www.w3.org/1999/XSL/Format">
                  <xsl:output method="xml" indent="yes"/>
                  <xsl:template match="/">
                    <fo:root>
                      <fo:layout-master-set>
                        <fo:simple-page-master master-name="A4-portrait"
                              page-height="29.7cm" page-width="21.0cm" margin="2cm">
                          <fo:region-body/>
                        </fo:simple-page-master>
                      </fo:layout-master-set>
                      <fo:page-sequence master-reference="A4-portrait">
                        <fo:flow flow-name="xsl-region-body">
                          <fo:block>
                            Hello, <xsl:value-of select="note/to"/>!
                          </fo:block>
                        </fo:flow>
                      </fo:page-sequence>
                    </fo:root>
                  </xsl:template>
                </xsl:stylesheet>""";

        FormDataMultiPart form = new FormDataMultiPart();
        form.bodyPart(new StreamDataBodyPart("xml", new ByteArrayInputStream(xml.getBytes())));
        form.bodyPart(new StreamDataBodyPart("xslt", new ByteArrayInputStream(xslt.getBytes())));

        Response response = target("documents")
                .register(MultiPartFeature.class)
                .request()
                .post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA_TYPE));

        assertEquals(200, response.getStatus());

        byte[] content = response.readEntity(byte[].class);

        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            try (PDDocument pdf = PDDocument.load(inputStream)) {
                assertEquals(1, pdf.getNumberOfPages());

                String text = new PDFTextStripper().getText(pdf).trim();
                assertEquals("Hello, Tove!", text);
            }
        }
    }

}
