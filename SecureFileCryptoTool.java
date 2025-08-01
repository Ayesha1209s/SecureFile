package secureFileCryptoToolProject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class SecureFileCryptoTool extends JFrame implements ActionListener {
    private JTextField fileField, passwordField;
    private JTextArea statusArea;
    private JButton browseButton, encryptButton, decryptButton;
    private File selectedFile;

    public SecureFileCryptoTool() {
        setTitle("Secure File Encryption and Decryption Tool");
        setSize(600, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(3, 1));

        // File Selection
        JPanel filePanel = new JPanel();
        filePanel.add(new JLabel("File: "));
        fileField = new JTextField(30);
        fileField.setEditable(false);
        filePanel.add(fileField);
        browseButton = new JButton("Browse");
        browseButton.addActionListener(this);
        filePanel.add(browseButton);

        // Password Input
        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("Password: "));
        passwordField = new JTextField(30);
        passwordPanel.add(passwordField);

        // Action Buttons
        JPanel buttonPanel = new JPanel();
        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");
        encryptButton.addActionListener(this);
        decryptButton.addActionListener(this);
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        topPanel.add(filePanel);
        topPanel.add(passwordPanel);
        topPanel.add(buttonPanel);

        // Status Area
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == browseButton) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                fileField.setText(selectedFile.getAbsolutePath());
            }
        } else if (e.getSource() == encryptButton) {
            if (selectedFile != null && !passwordField.getText().isEmpty()) {
                executeOpenSSLCommand("encrypt");
            } else {
                updateStatus("Please select a file and enter a password.");
            }
        } else if (e.getSource() == decryptButton) {
            if (selectedFile != null && !passwordField.getText().isEmpty()) {
                executeOpenSSLCommand("decrypt");
            } else {
                updateStatus("Please select a file and enter a password.");
            }
        }
    }

    private void executeOpenSSLCommand(String mode) {
        try {
            String inputPath = selectedFile.getAbsolutePath();
            String outputPath = inputPath + (mode.equals("encrypt") ? ".enc" : ".dec");
            String password = passwordField.getText();

            String[] command;
            if (mode.equals("encrypt")) {
                command = new String[]{"openssl", "enc", "-aes-256-cbc", "-salt", "-in", inputPath, "-out", outputPath, "-k", password};
            } else {
                command = new String[]{"openssl", "enc", "-d", "-aes-256-cbc", "-in", inputPath, "-out", outputPath, "-k", password};
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            int exitCode = process.waitFor();

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            if (exitCode == 0) {
                updateStatus(mode.equals("encrypt") ? "Encryption Successful." : "Decryption Successful.");
            } else {
                updateStatus((mode.equals("encrypt") ? "Encryption Failed. " : "Decryption Failed. ") + errorOutput.toString());
            }

        } catch (Exception ex) {
            updateStatus("Error: " + ex.getMessage());
        }
    }

    private void updateStatus(String message) {
        statusArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SecureFileCryptoTool().setVisible(true);
        });
    }
}
