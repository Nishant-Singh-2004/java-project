package ChatApp.src.server;



import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 5000;
    private static Map<String, Set<ClientHandler>> chatRooms = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Chat Server running on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void addClientToRoom(String room, ClientHandler client) {
        chatRooms.computeIfAbsent(room, k -> new HashSet<>()).add(client);
    }

    public static synchronized void removeClientFromRoom(String room, ClientHandler client) {
        if (chatRooms.containsKey(room)) {
            chatRooms.get(room).remove(client);
        }
    }

    public static synchronized void broadcast(String room, String message) {
        if (chatRooms.containsKey(room)) {
            for (ClientHandler client : chatRooms.get(room)) {
                client.sendMessage(message);
            }
        }
    }
}
