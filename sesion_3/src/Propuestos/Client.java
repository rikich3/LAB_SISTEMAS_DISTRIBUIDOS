import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The Client that can be run as a console
 */
public class Client {
    // notification
    private String notif = " *** ";
    // for I/O
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;
    private String server, username;
    private int port;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Constructor to set below things
     * server: the server address
     * port: the port number
     * username: the username
     */
    Client(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    /**
     * To start the chat
     */
    public boolean start() {
        try {
            socket = new Socket(server, port);
        } catch (Exception ec) {
            display("Error connectiong to server:" + ec);
            return false;
        }
        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        /* Creating both Data Stream */
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the server
        new ListenFromServer().start();

        // Send our username to the server this is the only message that we
        // will send as a String. All other messages will be ChatMessage objects
        try {
            sOutput.writeObject(username);
        } catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }

        // success we inform the caller that it worked
        return true;
    }

    /**
     * To send a message to the console
     */
    private void display(String msg) {
        System.out.println(msg);
    }

    /**
     * To send a message to the server
     */
    void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    /**
     * When something goes wrong
     * Close the Input/Output streams and disconnect
     */
    private void disconnect() {
        try {
            if (sInput != null)
                sInput.close();
        } catch (IOException e) {
            // nothing
        }
        try {
            if (sOutput != null)
                sOutput.close();
        } catch (IOException e) {
            // nothing
        }
        try {
            if (socket != null)
                socket.close();
        } catch (Exception e) {
            // nothing
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter server address (default: localhost): ");
        String server = scanner.nextLine().trim();
        if (server.isEmpty())
            server = "localhost";

        System.out.print("Enter port (default: 5001): ");
        int port = 5001;
        try {
            port = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            // keep default
        }

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty())
            username = "Anonymous";

        Client client = new Client(server, port, username);
        if (!client.start())
            return;

        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String message = input.nextLine();
            if (message.equalsIgnoreCase("QUIT")) {
                client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
                break;
            }
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, message));
        }
        client.disconnect();
    }

    /**
     * a class that waits for the message from the server and append them to the
     * JTextArea
     * if it is a message it append "username: message"
     * if it is a logout it remove the user from the list
     */
    class ListenFromServer extends Thread {
        public void run() {
            while (true) {
                try {
                    Object obj = sInput.readObject();
                    if (obj instanceof String) {
                        String message = (String) obj;
                        display(message);
                    } else if (obj instanceof ChatMessage) {
                        ChatMessage cm = (ChatMessage) obj;
                        switch (cm.getType()) {
                            case ChatMessage.MESSAGE:
                                display(cm.getMessage());
                                break;
                            case ChatMessage.LOGOUT:
                                display(cm.getMessage());
                                break;
                        }
                    }
                } catch (IOException e) {
                    display("Server has closed the connection: " + e);
                    break;
                } catch (ClassNotFoundException e2) {
                    // nothing
                }
            }
        }
    }
}
