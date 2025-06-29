package com.pdf.convert.resource;

import com.pdf.convert.exception.ConversionException;
import com.pdf.convert.service.PdfConversionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * REST endpoint for PDF to DOCX conversion.
 */
@Path("/api/convert/pdf-to-docx")
public class PdfToDocxResource {

    private static final Logger LOG = Logger.getLogger(PdfToDocxResource.class);
    
    @Inject
    PdfConversionService conversionService;
    
    @ConfigProperty(name = "pdf.convert.max-file-size", defaultValue = "10485760")
    long maxFileSize;

    /**
     * Converts a PDF file to DOCX format.
     *
     * @param file The uploaded PDF file
     * @param options JSON string with conversion options (optional)
     * @return Response containing the converted DOCX file
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public Response convertPdfToDocx(
            @RestForm("file") FileUpload file,
            @RestForm(value = "options", defaultValue = "{}") String options) {
        
        LOG.info("Received conversion request for file: " + file.fileName());
        
        try {
            // Validate file size
            if (file.size() > maxFileSize) {
                LOG.warn("File size exceeds maximum allowed: " + file.size() + " > " + maxFileSize);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("File size exceeds maximum allowed (" + maxFileSize / (1024 * 1024) + " MB)")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            
            // Validate file type
            if (!file.contentType().equalsIgnoreCase("application/pdf")) {
                LOG.warn("Invalid file type: " + file.contentType());
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                        .entity("Only PDF files are supported")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            
            // Convert the file
            try (InputStream inputStream = new FileInputStream(file.uploadedFile().toFile())) {
                byte[] docxData = conversionService.convertPdfToDocx(inputStream, options);
                
                // Return the converted file
                String filename = generateOutputFilename(file.fileName());
                return Response.ok(docxData)
                        .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                        .build();
            }
        } catch (Exception e) {
            LOG.error("Error during PDF to DOCX conversion", e);
            throw new ConversionException("Failed to convert PDF to DOCX: " + e.getMessage(), e, "CONV-ERR-001");
        }
    }
    
    /**
     * Generates an output filename based on the input filename.
     *
     * @param inputFilename The input filename
     * @return The output filename
     */
    private String generateOutputFilename(String inputFilename) {
        if (inputFilename == null || inputFilename.isEmpty()) {
            return "converted.docx";
        }
        
        // Replace .pdf extension with .docx
        if (inputFilename.toLowerCase().endsWith(".pdf")) {
            return inputFilename.substring(0, inputFilename.length() - 4) + ".docx";
        }
        
        // Add .docx extension if no .pdf extension
        return inputFilename + ".docx";
    }
} 