package ChatApp.src.client;

import javax.swing.*;

import ChatApp.src.database.DatabaseManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField chatRoomField;

    public LoginWindow() {
        setTitle("Chat App Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Chat Room:"));
        chatRoomField = new JTextField();
        add(chatRoomField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginHandler());
        add(loginButton);

        setVisible(true);
    }

    private class LoginHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String chatRoom = chatRoomField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || chatRoom.isEmpty()) {
                JOptionPane.showMessageDialog(LoginWindow.this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (DatabaseManager.authenticateUser(username, password)) {
                JOptionPane.showMessageDialog(LoginWindow.this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close login window
                new ChatClient(username, chatRoom); // Open chat client
            } else {
                JOptionPane.showMessageDialog(LoginWindow.this, "Invalid Credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginWindow::new);
    }
}
