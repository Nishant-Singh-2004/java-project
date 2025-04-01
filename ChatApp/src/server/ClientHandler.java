package ChatApp.src.server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private String chatRoom;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            username = in.readLine();
            chatRoom = in.readLine();
            ChatServer.addClientToRoom(chatRoom, this);
            ChatServer.broadcast(chatRoom, username + " joined " + chatRoom);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("FILE_TRANSFER:")) {
                    receiveFile(message.replace("FILE_TRANSFER:", ""));
                } else {
                    ChatServer.broadcast(chatRoom, username + ": " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ChatServer.removeClientFromRoom(chatRoom, this);
            ChatServer.broadcast(chatRoom, username + " left the chat.");
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void receiveFile(String fileName) {
        try {
            File file = new File("server_files/" + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = socket.getInputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) > 0) {
                fos.write(buffer, 0, bytesRead);
            }

            fos.close();
            ChatServer.broadcast(chatRoom, username + " sent a file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
