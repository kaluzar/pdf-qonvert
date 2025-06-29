package com.pdf.convert.config;

import com.aspose.pdf.License;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Configuration class for Aspose.PDF library.
 * Handles license loading at application startup.
 */
@ApplicationScoped
public class AsposeConfig {

    private static final Logger LOG = Logger.getLogger(AsposeConfig.class);

    @ConfigProperty(name = "pdf.convert.license.path")
    String licensePath;

    /**
     * Initializes Aspose.PDF with the license when the application starts.
     *
     * @param event The startup event
     */
    void onStart(@Observes StartupEvent event) {
        try {
            LOG.info("Initializing Aspose.PDF license from: " + licensePath);
            
            License license = new License();
            InputStream licenseStream = null;
            
            // Try as a file path first
            File licenseFile = new File(licensePath);
            if (licenseFile.exists()) {
                licenseStream = new FileInputStream(licenseFile);
            } else {
                // Try as a classpath resource
                licenseStream = getClass().getClassLoader().getResourceAsStream(licensePath);
            }
            
            if (licenseStream != null) {
                license.setLicense(licenseStream);
                licenseStream.close();
                LOG.info("Aspose.PDF license set successfully");
            } else {
                LOG.warn("Aspose.PDF license file not found. Running in evaluation mode.");
            }
        } catch (Exception e) {
            LOG.error("Failed to set Aspose.PDF license", e);
        }
    }
} 