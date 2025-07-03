package com.invoice.Invoice_System.service;

import com.invoice.Invoice_System.exception.PdfGenerationException;
import com.invoice.Invoice_System.model.Invoice;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Service
public class PdfGeneratorService {

    private final SpringTemplateEngine templateEngine;

    public PdfGeneratorService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String generateInvoicePdf(Invoice invoice) {
        try {
            // 1. Setup Thymeleaf context with data
            Context context = new Context();
            context.setVariable("invoice", invoice);

            // 2. Render Thymeleaf template into HTML string
            String htmlContent = templateEngine.process("invoice", context);

            // 3. Create output directory if not exists
            String outputDir = "invoices/";
            File folder = new File(outputDir);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 4. Define output file path
            String outputPath = outputDir + "invoice_" + invoice.getId() + ".pdf";
            try (OutputStream outputStream = new FileOutputStream(outputPath)) {

                // 5. Generate PDF from HTML
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(htmlContent, null); // baseUri null means local HTML only
                builder.toStream(outputStream);
                builder.run();
            }

            return outputPath;

        } catch (Exception e) {
            throw new PdfGenerationException("Failed to generate invoice PDF: " + e.getMessage());
        }
    }
}
