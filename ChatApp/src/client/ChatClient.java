package ChatApp.src.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private String chatRoom;
    private JTextArea chatArea;
    private JTextField messageField;
    private JFrame frame;

    public ChatClient(String username, String chatRoom) {
        this.username = username;
        this.chatRoom = chatRoom;
        initializeUI();
        connectToServer();
    }

    private void initializeUI() {
        frame = new JFrame("Chat Room: " + chatRoom);
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.addActionListener(e -> sendMessage());
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        JButton fileButton = new JButton("Send File");
        fileButton.addActionListener(e -> sendFile());

        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        panel.add(fileButton, BorderLayout.WEST);

        frame.add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(username);
            out.println(chatRoom);
            new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                chatArea.append(message + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            messageField.setText("");
        }
    }

    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                out.println("FILE_TRANSFER:" + file.getName());
                FileInputStream fis = new FileInputStream(file);
                OutputStream os = socket.getOutputStream();

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) > 0) {
                    os.write(buffer, 0, bytesRead);
                }
                fis.close();
                chatArea.append("You sent a file: " + file.getName() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
