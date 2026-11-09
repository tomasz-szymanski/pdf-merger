package com.szymaniaq.pdf;

import com.szymaniaq.pdf.ui.PdfUtilsWindow;

import javax.swing.*;

public class PdfUtilsApp {

    static void main() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // fall back to the default look and feel
            }
            new PdfUtilsWindow().show();
        });
    }
}
