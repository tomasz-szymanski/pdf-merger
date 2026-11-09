package com.szymaniaq.pdf.splitter;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;

public class PdfSplitter {

    /**
     * Creates a new PDF containing pages startPage..endPage (1-based, inclusive) of the input,
     * saved next to it as {@code <original_name>-pages-<start>-to-<end>.pdf}.
     *
     * @return path of the created file
     */

    private final static String OUTPUT_FILE_NAME_FORMAT = "%s-pages-%d-to-%d.pdf";

    public Path extract(Path input, int startPage, int endPage) {
        String baseName = input.getFileName().toString().replaceFirst("(?i)\\.pdf$", "");
        Path output = input.resolveSibling(String.format(OUTPUT_FILE_NAME_FORMAT, baseName, startPage, endPage));

        try (PDDocument source = Loader.loadPDF(input.toFile())) {
            int pageCount = source.getNumberOfPages();
            if (startPage < 1 || endPage < startPage || endPage > pageCount) {
                throw new IllegalArgumentException(
                        "Invalid page range " + startPage + "-" + endPage + " for document with " + pageCount + " pages");
            }

            Splitter splitter = new Splitter();
            splitter.setStartPage(startPage);
            splitter.setEndPage(endPage);
            splitter.setSplitAtPage(endPage - startPage + 1); // whole range goes into one document

            try (PDDocument extracted = splitter.split(source).getFirst()) {
                extracted.save(output.toFile());
            }
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract pages from " + input, e);
        }
    }
}
