package com.pdf.convert.health;

import com.pdf.convert.service.PdfConversionService;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Health check for the PDF conversion service.
 * Verifies that the service is ready to accept requests.
 */
@Readiness
@ApplicationScoped
public class ConversionServiceHealthCheck implements HealthCheck {

    private static final Logger LOG = Logger.getLogger(ConversionServiceHealthCheck.class);
    
    @Inject
    PdfConversionService conversionService;

    @Override
    public HealthCheckResponse call() {
        try {
            // Check if Aspose dependencies are available
            Class.forName("com.aspose.pdf.Document");
            Class.forName("com.aspose.pdf.DocSaveOptions");
            
            return HealthCheckResponse.up("Conversion service is ready");
        } catch (Exception e) {
            LOG.error("Health check failed", e);
            return HealthCheckResponse.down("Conversion service is not ready: " + e.getMessage());
        }
    }
} 