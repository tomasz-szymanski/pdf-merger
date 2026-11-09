package com.szymaniaq.pdf.ui;

import com.szymaniaq.pdf.merger.PdfMerger;
import com.szymaniaq.pdf.splitter.PdfSplitter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class PdfUtilsWindow {

    private static final String TITLE = "PDF Utils";

    private final JFrame frame = new JFrame(TITLE);
    private final DefaultListModel<File> filesModel = new DefaultListModel<>();
    private final JList<File> filesList = new JList<>(filesModel);
    private final JTextField destinationField = new JTextField(30);
    private final JTextField fileNameField = new JTextField("merged.pdf", 30);
    private final JTextField startPageField = new JTextField(5);
    private final JTextField endPageField = new JTextField(5);
    private final JButton mergeButton = new JButton("Merge");
    private final JButton extractButton = new JButton("Extract");

    public void show() {
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(createFilesPanel(), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(8, 8));
        south.add(createExtractPanel(), BorderLayout.NORTH);
        south.add(createDestinationPanel(), BorderLayout.CENTER);
        south.add(createButtonsPanel(), BorderLayout.SOUTH);
        content.add(south, BorderLayout.SOUTH);

        frame.setContentPane(content);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 520);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createFilesPanel() {
        filesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton chooseButton = new JButton("Choose files...");
        chooseButton.addActionListener(_ -> chooseSourceFiles());

        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Files"));
        panel.add(new JScrollPane(filesList), BorderLayout.CENTER);
        panel.add(chooseButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createExtractPanel() {
        extractButton.addActionListener(_ -> extract());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Extract pages from selected file"));
        panel.add(new JLabel("Start page:"));
        panel.add(startPageField);
        panel.add(new JLabel("End page:"));
        panel.add(endPageField);
        panel.add(extractButton);
        return panel;
    }

    private JPanel createDestinationPanel() {
        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(_ -> chooseDestinationFolder());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Merge destination"));
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
        chooser.setDialogTitle("Choose PDF files");
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new FileNameExtensionFilter("PDF files", "pdf"));

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            filesModel.clear();
            for (File file : chooser.getSelectedFiles()) {
                filesModel.addElement(file);
            }
            if (filesModel.size() == 1) {
                filesList.setSelectedIndex(0);
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

        runInBackground(mergeButton, "Merge", () -> {
            if (!new PdfMerger().merge(sources, outputFile)) {
                throw new IllegalStateException("Nothing was merged.");
            }
            return outputFile;
        });
    }

    private void extract() {
        File source = filesList.getSelectedValue();
        if (source == null) {
            showError("Select a file to extract pages from.");
            return;
        }

        int startPage;
        int endPage;
        try {
            startPage = Integer.parseInt(startPageField.getText().trim());
            endPage = Integer.parseInt(endPageField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Start page and end page must be numbers.");
            return;
        }
        if (startPage < 1 || endPage < startPage) {
            showError("Start page must be at least 1 and not greater than end page.");
            return;
        }

        runInBackground(extractButton, "Extract",
                () -> new PdfSplitter().extract(source.toPath(), startPage, endPage).toString());
    }

    private void runInBackground(JButton button, String actionName, Callable<String> task) {
        button.setEnabled(false);
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                button.setEnabled(true);
                try {
                    JOptionPane.showMessageDialog(frame, "Saved to " + get(), "Done",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    showError(actionName + " failed: " + cause.getMessage());
                }
            }
        }.execute();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, TITLE, JOptionPane.ERROR_MESSAGE);
    }
}
