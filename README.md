# PDF Utils

A small Windows desktop app (Java Swing + [Apache PDFBox](https://pdfbox.apache.org/)) for everyday PDF tasks:

- **Merge** several PDF files into one.
- **Extract** a range of pages from a PDF into a new file.

## Requirements

- Java 25 (JDK to build, JRE/JDK to run)
- Maven 3 — either on `PATH` or the one bundled with IntelliJ IDEA (used automatically by `script\build.bat`)

## Build and run

```bat
script\build.bat
script\run.bat
```

`build.bat` creates `target\pdf-utils-1.0-SNAPSHOT.jar` with PDFBox bundled in (tests are skipped).
`run.bat` starts the app without a console window.

You can also run the jar directly:

```bat
java -jar target\pdf-utils-1.0-SNAPSHOT.jar
```

or start `com.szymaniaq.pdf.PdfUtilsApp` from IntelliJ.

## Usage

1. Click **Choose files...** and pick one or more PDF files.

### Merge

2. Under **Merge destination**, choose a folder and enter the output file name (default `merged.pdf`).
3. Click **Merge**. Files are merged in the order shown in the list.

### Extract pages

2. Select a file in the list (if only one file was chosen, it is selected automatically).
3. Enter **Start page** and **End page** (1-based, inclusive).
4. Click **Extract**.

The new file is saved next to the original as `<original_name>-pages-<start>-to-<end>.pdf`,
e.g. `report.pdf`, pages 1–3 → `report-pages-1-to-3.pdf`. The original file is never modified;
an existing file with the same output name is overwritten.

## Project structure

| Path | Description |
|------|-------------|
| `src/main/java/com/szymaniaq/pdf/PdfUtilsApp.java` | Entry point |
| `src/main/java/com/szymaniaq/pdf/ui/PdfUtilsWindow.java` | Swing window |
| `src/main/java/com/szymaniaq/pdf/merger/PdfMerger.java` | Merging (PDFBox `PDFMergerUtility`) |
| `src/main/java/com/szymaniaq/pdf/splitter/PdfSplitter.java` | Page extraction (PDFBox `Splitter`) |
| `src/main/resources/docs/` | Sample PDFs used by tests |
| `script/` | Build and run scripts, desktop shortcut |
| `icon/` | App icon and the script that generates it |

## Tests

```bat
mvn test
```

Tests copy the sample PDFs from `docs/` into a temporary folder, so they don't depend on local files
and leave nothing behind.
