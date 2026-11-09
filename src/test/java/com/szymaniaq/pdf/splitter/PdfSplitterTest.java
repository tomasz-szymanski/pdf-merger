package com.szymaniaq.pdf.splitter;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfSplitterTest {

    @Test
    void split() {

        PdfSplitter splitter = new PdfSplitter();
        Path output = splitter.extract(
                Path.of("C:\\Users\\szyma\\Pulpit\\NNW Alek 2026\\1.pdf"),
                1,
                2);

        assertTrue(output.toFile().exists());

    }
}