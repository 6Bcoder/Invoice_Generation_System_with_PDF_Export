package com.invoice.Invoice_System.exception;

public class PdfGenerationException extends RuntimeException {
  public PdfGenerationException(String message) {
    super(message);
  }
}
