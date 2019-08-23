# pdfgenerator


Microservice written in Java that uses <a href="https://xmlgraphics.apache.org/fop/">Apache FOP</a> to create PDFs based on XML and XSLT.

### Prerequisites

* Java 11 or higher
* Apache Maven

### Installation

Clone the Git repository from Github:

    $ git clone https://github.com/wiigen/pdfgenerator.git

Build with Maven:

    $ cd pdfgenerator
    $ mvn clean install

Run the application. This will start the server on port 8080 (change to what you want).

    $ java -jar target/pdfgenerator-1.0.jar 8080

Use for example a basic HTML form to interact with the service:

    <!DOCTYPE html>
    <html lang="en">
    
        <head>
            <meta charset="utf-8">
            <title>Very basic form</title>
        </head>
    
        <body>
            <form action="http://localhost:8080/document" method="POST" enctype="multipart/form-data">
                <label for="xml">XML input:</label> <input type="file" id="xml" name="xml" accept=".xml">
                <label for="xslt">XSLT input:</label> <input type="file" id="xslt" name="xslt" accept=".xsl">
                <input type="submit">
            </form>
        </body>
    </html>

Example on XML input:

    <?xml version="1.0" encoding="utf-8"?>
    <note>
        <to>Catherine</to>
        <from>Jake</from>
        <heading>Reminder</heading>
        <body>Don't forget me this weekend!</body>
    </note>

Example on XSLT input:

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
    </xsl:stylesheet>

This will download a PDF document based on the XML and XSLT. With the example XML/XSLT above it will print:

    Hello, Catherine!

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.