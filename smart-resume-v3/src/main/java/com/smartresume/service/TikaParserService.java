package com.smartresume.service;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts raw text from uploaded files using Apache Tika.
 * Supports PDF, DOCX, DOC, TXT, and any format Tika handles.
 */
@Service
public class TikaParserService {

    private final Tika tika = new Tika();

    /**
     * Extract all text from an uploaded file.
     * Tika auto-detects the format — no extension check needed.
     */
    public String extractText(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            String text = tika.parseToString(inputStream);
            return text != null ? text.trim() : "";
        } catch (IOException | TikaException e) {
            // Return empty string — caller handles the fallback gracefully
            return "";
        }
    }

    /**
     * Quick check: is this file type something Tika can parse?
     * (PDF and DOCX are always supported; this is a safety guard.)
     */
    public boolean isSupportedFormat(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null) return false;
        String lower = name.toLowerCase();
        return lower.endsWith(".pdf")
                || lower.endsWith(".docx")
                || lower.endsWith(".doc")
                || lower.endsWith(".txt")
                || lower.endsWith(".rtf")
                || lower.endsWith(".odt");
    }
}
