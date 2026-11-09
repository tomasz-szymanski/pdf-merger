package com.szymaniaq.pdf.merger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class PdfMergerTest {

    @Test
    void testMergingTwoDocs_should_succeed() {

        PdfMerger merger = new PdfMerger();

        boolean result =
                merger.merge(
                        List.of(
                                "C:\\Users\\szyma\\Pulpit\\NNW Alek 2026\\1.pdf",
                                "C:\\Users\\szyma\\Pulpit\\NNW Alek 2026\\2.pdf"),
                        "C:\\Users\\szyma\\Pulpit\\NNW Alek 2026\\result.pdf");

        Assertions.assertTrue(result);
    }
}