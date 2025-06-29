package com.pdf.convert.service;

import com.aspose.pdf.Document;
import com.aspose.pdf.DocSaveOptions;
import com.aspose.pdf.SaveFormat;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Service for converting PDF files to DOCX format using Aspose.PDF.
 */
@ApplicationScoped
public class PdfConversionService {

    private static final Logger LOG = Logger.getLogger(PdfConversionService.class);

    @ConfigProperty(name = "pdf.convert.docx.default-options")
    String defaultOptionsJson;
    
    @ConfigProperty(name = "pdf.convert.timeout", defaultValue = "30000")
    long conversionTimeout;

    /**
     * Converts a PDF file to DOCX format with default options.
     *
     * @param pdfData The PDF file data as an input stream
     * @return A byte array containing the converted DOCX file
     * @throws Exception If conversion fails
     */
    @Timeout(value = 30000)
    @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.6, delay = 10000)
    public byte[] convertPdfToDocx(InputStream pdfData) throws Exception {
        return convertPdfToDocx(pdfData, defaultOptionsJson);
    }

    /**
     * Converts a PDF file to DOCX format with custom options.
     *
     * @param pdfData The PDF file data as an input stream
     * @param optionsJson JSON string with conversion options
     * @return A byte array containing the converted DOCX file
     * @throws Exception If conversion fails
     */
    @Timeout(value = 30000)
    @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.6, delay = 10000)
    @Fallback(fallbackMethod = "conversionFallback")
    public byte[] convertPdfToDocx(InputStream pdfData, String optionsJson) throws Exception {
        LOG.debug("Starting PDF to DOCX conversion");
        long startTime = System.currentTimeMillis();
        
        // Parse options
        JsonObject options = parseOptions(optionsJson);
        
        // Load the PDF document
        Document pdfDocument = new Document(pdfData);
        LOG.debug("PDF document loaded with " + pdfDocument.getPages().size() + " pages");
        
        // Configure save options
        DocSaveOptions saveOptions = new DocSaveOptions();
        configureDocSaveOptions(saveOptions, options);
        
        // Convert to DOCX
        ByteArrayOutputStream docxOutputStream = new ByteArrayOutputStream();
        pdfDocument.save(docxOutputStream, SaveFormat.DocX);
        
        // Calculate conversion time
        long endTime = System.currentTimeMillis();
        LOG.info("PDF to DOCX conversion completed in " + (endTime - startTime) + "ms for " + 
                pdfDocument.getPages().size() + " pages");
        
        return docxOutputStream.toByteArray();
    }
    
    /**
     * Fallback method for conversion failures.
     * This method will be called if the primary conversion method fails.
     * 
     * @param pdfData The PDF file data as an input stream
     * @param optionsJson JSON string with conversion options
     * @return A byte array containing the converted DOCX file
     * @throws Exception If fallback conversion also fails
     */
    public byte[] conversionFallback(InputStream pdfData, String optionsJson) throws Exception {
        LOG.warn("Using fallback conversion method");
        
        // Load the PDF document
        Document pdfDocument = new Document(new ByteArrayInputStream(pdfData.readAllBytes()));
        
        // Use minimal options for fallback
        DocSaveOptions saveOptions = new DocSaveOptions();
        saveOptions.setMode(DocSaveOptions.RecognitionMode.Flow);
        
        // Convert to DOCX
        ByteArrayOutputStream docxOutputStream = new ByteArrayOutputStream();
        pdfDocument.save(docxOutputStream, SaveFormat.DocX);
        
        return docxOutputStream.toByteArray();
    }
    
    /**
     * Parses JSON options for conversion.
     *
     * @param optionsJson JSON string with conversion options
     * @return JsonObject containing the parsed options
     */
    private JsonObject parseOptions(String optionsJson) {
        try (JsonReader reader = Json.createReader(new StringReader(optionsJson))) {
            return reader.readObject();
        } catch (Exception e) {
            LOG.warn("Failed to parse options JSON: " + e.getMessage());
            // Return empty JSON object for default settings
            return Json.createObjectBuilder().build();
        }
    }
    
    /**
     * Configures DocSaveOptions based on JSON options.
     *
     * @param saveOptions The DocSaveOptions to configure
     * @param options JsonObject containing the configuration options
     */
    private void configureDocSaveOptions(DocSaveOptions saveOptions, JsonObject options) {
        // Set recognition mode
        if (options.containsKey("mode")) {
            String mode = options.getString("mode").toLowerCase();
            if ("flow".equals(mode)) {
                saveOptions.setMode(DocSaveOptions.RecognitionMode.Flow);
            } else if ("textbox".equals(mode)) {
                saveOptions.setMode(DocSaveOptions.RecognitionMode.Textbox);
            }
        } else {
            // Default to Flow mode
            saveOptions.setMode(DocSaveOptions.RecognitionMode.Flow);
        }
        
        // Set bullet recognition
        if (options.containsKey("recognizeBullets")) {
            saveOptions.setRecognizeBullets(options.getBoolean("recognizeBullets"));
        }
        
        // Set relative horizontal proximity
        if (options.containsKey("relativeHorizontalProximity")) {
            saveOptions.setRelativeHorizontalProximity(
                    (float) options.getJsonNumber("relativeHorizontalProximity").doubleValue());
        }
    }
} 