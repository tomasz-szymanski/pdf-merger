package com.szymaniaq.pdf.splitter;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfSplitterTest {

    private static final String SAMPLE_PDF = "/docs/sample-file-1.pdf";

    @TempDir
    Path tempDir;

    @Test
    void extract_should_create_new_file_with_selected_pages() throws IOException {
        Path input = copySampleToTempDir();

        Path output = new PdfSplitter().extract(input, 2, 4);

        assertEquals(tempDir.resolve("sample-file-1-pages-2-to-4.pdf"), output);
        assertTrue(Files.exists(output));
        try (PDDocument extracted = Loader.loadPDF(output.toFile())) {
            assertEquals(3, extracted.getNumberOfPages());
        }
    }

    @Test
    void extract_should_not_modify_original_file() throws IOException {
        Path input = copySampleToTempDir();
        byte[] before = Files.readAllBytes(input);

        new PdfSplitter().extract(input, 1, 2);

        assertArrayEquals(before, Files.readAllBytes(input));
    }

    @Test
    void extract_should_fail_when_end_page_is_beyond_document() throws IOException {
        Path input = copySampleToTempDir();

        assertThrows(IllegalArgumentException.class, () -> new PdfSplitter().extract(input, 1, 6));
    }

    private Path copySampleToTempDir() throws IOException {
        Path target = tempDir.resolve("sample-file-1.pdf");
        try (InputStream in = getClass().getResourceAsStream(SAMPLE_PDF)) {
            Files.copy(in, target);
        }
        return target;
    }
}
