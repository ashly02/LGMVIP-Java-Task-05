import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.standard.JobName;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MyFrame1 extends JFrame implements ActionListener {
    JMenuBar menubar;
    JMenu file, edit;
    JMenuItem copy, cut, paste, selectAll, newFile, openItem, saveItem, exitItem, printItem, saveasItem;
    JTextArea textarea;
    JScrollPane scrollPane;
    JButton saveAndSubmitButton;

    private File currentFile;

    public MyFrame1(File file1) {
        this.setTitle("Text Editor");
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textarea = new JTextArea();
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);
        textarea.setFont(new Font("Arial", Font.PLAIN, 20));

        scrollPane = new JScrollPane(textarea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(585, 540));

        menubar = new JMenuBar();

        file = new JMenu("File");
        edit = new JMenu("Edit");

        newFile = new JMenuItem("New File");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        saveasItem = new JMenuItem("Save As");
        printItem = new JMenuItem("Print");
        exitItem = new JMenuItem("Exit");

        copy = new JMenuItem("Copy");
        cut = new JMenuItem("Cut");
        paste = new JMenuItem("Paste");
        selectAll = new JMenuItem("Select All");

        newFile.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        saveasItem.addActionListener(this);
        exitItem.addActionListener(this);
        printItem.addActionListener(this);

        cut.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        selectAll.addActionListener(this);

        file.add(newFile);
        file.add(openItem);
        file.add(saveItem);
        file.add(saveasItem);
        file.add(printItem);
        file.add(exitItem);

        edit.add(copy);
        edit.add(cut);
        edit.add(paste);
        edit.add(selectAll);

        menubar.add(file);
        menubar.add(edit);

        saveAndSubmitButton = new JButton("Save & Submit");
        saveAndSubmitButton.addActionListener(this);
        saveAndSubmitButton.setPreferredSize(new Dimension(120, 30));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveAndSubmitButton);

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);

        this.setJMenuBar(menubar);
        this.setVisible(true);

        currentFile = file1;
        loadFile(currentFile);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == cut) {
            textarea.cut();
        }
        if (evt.getSource() == copy) {
            textarea.copy();
        }
        if (evt.getSource() == paste) {
            textarea.paste();
        }
        if (evt.getSource() == selectAll) {
            textarea.selectAll();
        }
        if (evt.getSource() == newFile) {
            new NextFrame();
        }
        if (evt.getSource() == openItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
            fileChooser.setFileFilter(filter);

            int response = fileChooser.showOpenDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
                if (currentFile.isFile()) {
                    new MyFrame1(currentFile);
                }
            }
        }
        if (evt.getSource() == saveItem) {
            saveFile(currentFile, false);
        }
        if (evt.getSource() == saveAndSubmitButton) {
            saveFile(currentFile, true);
        }
        if (evt.getSource() == printItem) {
            PrintRequestAttributeSet printAttributes = new HashPrintRequestAttributeSet();
            printAttributes.add(new JobName("Print Job", null));
            printAttributes.add(DialogTypeSelection.NATIVE);

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(textarea.getPrintable(null, null));
            boolean doPrint = job.printDialog(printAttributes);

            if (doPrint) {
                try {
                    job.print(printAttributes);
                } catch (PrinterException e2) {
                    e2.printStackTrace();
                }
            }
        }
        if (evt.getSource() == saveasItem) {
            saveAsFile();
        }

        if (evt.getSource() == exitItem) {
            this.dispose();
        }

    }

    private void saveFile(File file, boolean closeAfterSaving) {
        if (file != null) {
            try (PrintWriter fileOut = new PrintWriter(file)) {
                fileOut.println(textarea.getText());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            saveAsFile();
        }

        if (closeAfterSaving) {
            dispose();
        }
    }

    private void saveAsFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setDialogTitle("Save File");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        fileChooser.setFileFilter(filter);

        int response = fileChooser.showSaveDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".txt")) {
                selectedFile = new File(filePath + ".txt");
            }

            try (PrintWriter fileOut = new PrintWriter(selectedFile)) {
                fileOut.println(textarea.getText());
                currentFile = selectedFile;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadFile(File file) {
        try (Scanner fileIn = new Scanner(file)) {
            while (fileIn.hasNextLine()) {
                String line = fileIn.nextLine() + "\n";
                textarea.append(line);
            }
        } catch (FileNotFoundException e8) {
            e8.printStackTrace();
        }
    }
}
