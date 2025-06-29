package com.pdf.convert;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import java.io.File;

@QuarkusTest
public class PdfToDocxResourceTest {

    @Test
    public void testInvalidContentType() {
        given()
            .multiPart("file", new File("src/test/resources/test.txt"), "text/plain")
            .when()
            .post("/api/convert/pdf-to-docx")
            .then()
            .statusCode(415)
            .body(containsString("Only PDF files are supported"));
    }
    
    @Test
    public void testMissingFile() {
        given()
            .when()
            .post("/api/convert/pdf-to-docx")
            .then()
            .statusCode(400);
    }
    
    // Note: A full conversion test would require a real PDF file and Aspose.PDF library
    // which might not be available in CI/CD environments without a license
} 