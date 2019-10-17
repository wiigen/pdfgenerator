package com.jw.pdfgenerator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class AppTest {

    @Test
    public void shouldCreateDocumentWith200Ok() throws Exception {
        Random random = new Random();
        int port = random.nextInt(1000) + 8000;

        String[] args = { String.valueOf(port) };
        App app = new App(args);

        app.start();

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<note>\n"
                + "  <to>Tove</to>\n"
                + "  <from>Jani</from>\n"
                + "  <heading>Reminder</heading>\n"
                + "  <body>Don't forget me this weekend!</body>\n"
                + "</note>";

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

        HttpEntity data = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addBinaryBody("xml", xml.getBytes())
                .addBinaryBody("xslt", xslt.getBytes())
                .build();

        HttpUriRequest request = RequestBuilder
                .post("http://localhost:" + port+ "/api/documents")
                .setEntity(data)
                .build();

        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(request);

        assertEquals(200, response.getStatusLine().getStatusCode());

        app.stop();
    }
}
