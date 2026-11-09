package com.szymaniaq.pdf.ui;

import com.szymaniaq.pdf.merger.PdfMerger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MergerWindow {

    private final JFrame frame = new JFrame("PDF Merger");
    private final DefaultListModel<File> filesModel = new DefaultListModel<>();
    private final JTextField destinationField = new JTextField(30);
    private final JTextField fileNameField = new JTextField("merged.pdf", 30);
    private final JButton mergeButton = new JButton("Merge");

    public void show() {
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(createFilesPanel(), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(8, 8));
        south.add(createDestinationPanel(), BorderLayout.CENTER);
        south.add(createButtonsPanel(), BorderLayout.SOUTH);
        content.add(south, BorderLayout.SOUTH);

        frame.setContentPane(content);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 450);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createFilesPanel() {
        JList<File> filesList = new JList<>(filesModel);

        JButton chooseButton = new JButton("Choose files...");
        chooseButton.addActionListener(_ -> chooseSourceFiles());

        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Files to merge"));
        panel.add(new JScrollPane(filesList), BorderLayout.CENTER);
        panel.add(chooseButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDestinationPanel() {
        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(_ -> chooseDestinationFolder());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Destination"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        panel.add(new JLabel("Folder:"), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(destinationField, c);
        c.gridx = 2; c.weightx = 0;
        panel.add(browseButton, c);

        c.gridx = 0; c.gridy = 1;
        panel.add(new JLabel("File name:"), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(fileNameField, c);
        return panel;
    }

    private JPanel createButtonsPanel() {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> frame.dispose());
        mergeButton.addActionListener(_ -> merge());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(cancelButton);
        panel.add(mergeButton);
        return panel;
    }

    private void chooseSourceFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose PDF files to merge");
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new FileNameExtensionFilter("PDF files", "pdf"));

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            filesModel.clear();
            for (File file : chooser.getSelectedFiles()) {
                filesModel.addElement(file);
            }
        }
    }

    private void chooseDestinationFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose destination folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            destinationField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void merge() {
        if (filesModel.isEmpty()) {
            showError("Choose files to merge.");
            return;
        }
        String folder = destinationField.getText().trim();
        String fileName = fileNameField.getText().trim();
        if (folder.isEmpty() || fileName.isEmpty()) {
            showError("Choose destination folder and file name.");
            return;
        }
        if (!fileName.toLowerCase().endsWith(".pdf")) {
            fileName += ".pdf";
        }

        List<String> sources = new ArrayList<>();
        for (int i = 0; i < filesModel.size(); i++) {
            sources.add(filesModel.get(i).getAbsolutePath());
        }
        String outputFile = Path.of(folder, fileName).toString();

        mergeButton.setEnabled(false);
        createSwingWorker(sources, outputFile).execute();
    }

    private SwingWorker<Boolean, Void> createSwingWorker(List<String> sources, String outputFile) {
        return new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return new PdfMerger().merge(sources, outputFile);
            }

            @Override
            protected void done() {
                mergeButton.setEnabled(true);
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(frame, "Saved to " + outputFile, "Done",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        showError("Nothing was merged.");
                    }
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    showError("Merge failed: " + cause.getMessage());
                }
            }
        };
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "PDF Merger", JOptionPane.ERROR_MESSAGE);
    }
}
