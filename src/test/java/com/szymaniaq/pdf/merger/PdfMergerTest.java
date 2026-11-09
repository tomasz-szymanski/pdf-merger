package com.szymaniaq.pdf.merger;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class PdfMergerTest {

    @TempDir
    Path tempDir;

    @Test
    void testMergingTwoDocs_should_succeed() throws IOException {
        Path sample1 = copyResourceToTempDir("sample-file-1.pdf");
        Path sample2 = copyResourceToTempDir("sample-file-2.pdf");
        Path output = tempDir.resolve("result.pdf");
        PdfMerger merger = new PdfMerger();
        boolean result =
                merger.merge(
                        List.of(sample1.toString(), sample2.toString()),
                        output.toString());

        Assertions.assertTrue(result);
        try (PDDocument merged = Loader.loadPDF(output.toFile())) {
            Assertions.assertEquals(10, merged.getNumberOfPages());
        }
    }

    private Path copyResourceToTempDir(String fileName) throws IOException {
        Path target = tempDir.resolve(fileName);
        try (InputStream in = getClass().getResourceAsStream("/docs/" + fileName)) {
            Files.copy(in, target);
        }
        return target;
    }
}
