# pdfgenerator


Microservice written in Java that uses <a href="https://xmlgraphics.apache.org/fop/">Apache FOP</a> to create PDFs based on XML and XSLT.

### Prerequisites

* Java 8 or higher
* Apache Maven

### Installation

Build with Maven:

    mvn clean install

Run the application

    java -jar target/pdfgenerator-1.0.jar 8080

Use example form to interact with the service

	<form action="http://localhost:8080/document" method="POST">
		<input type="file" name="xml" accept=".xml">
		<input type="file" name="xslt" accept=".xsl">
		<input type="submit">
	</form>

This will download a PDF based on XML and XSLT.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.