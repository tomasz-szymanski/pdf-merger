package com.szymaniaq.pdf.merger;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessStreamCache;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PdfMerger {

    public boolean merge(List<String> sourceFilesToMerge, String outputFile) {

        if(Optional.ofNullable(sourceFilesToMerge).isEmpty() || sourceFilesToMerge.isEmpty()) {
            return false;
        }

        PDFMergerUtility utility = new PDFMergerUtility();
        sourceFilesToMerge.forEach(filePath -> {
            try {
                utility.addSource(filePath);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        utility.setDestinationFileName(outputFile);
        RandomAccessStreamCache.StreamCacheCreateFunction streamCacheFunction = IOUtils.createMemoryOnlyStreamCache();

        try {
            utility.mergeDocuments(streamCacheFunction);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}
