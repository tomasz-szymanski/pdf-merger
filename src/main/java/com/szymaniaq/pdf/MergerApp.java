package com.szymaniaq.pdf;

import com.szymaniaq.pdf.ui.MergerWindow;

import javax.swing.*;

public class MergerApp {

    static void main() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // fall back to the default look and feel
            }
            new MergerWindow().show();
        });
    }
}
