import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The server that can be run as a console
 * for a chat application
 */
public class Server {
    private static int uniqueId;
    private ArrayList<ClientThread> al;
    private int port;
    private boolean keepGoing;

    public Server(int port) {
        this.port = port;
        al = new ArrayList<ClientThread>();
    }

    public void start() {
        keepGoing = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);

            while (keepGoing) {
                Socket socket = serverSocket.accept();
                if (!keepGoing)
                    break;
                ClientThread t = new ClientThread(socket);
                al.add(t);
                t.start();
            }

            // I was asked to stop
            try {
                serverSocket.close();
                for (int i = 0; i < al.size(); ++i) {
                    ClientThread tc = al.get(i);
                    try {
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    } catch (IOException ioE) {
                        // not much I can do
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception closing the server and clients: " + e);
            }
        }
        // something went wrong
        catch (IOException e) {
            String msg = "Exception on new ServerSocket: " + e + "\n";
            System.out.println(msg);
        }
    }

    /*
     * For the GUI to stop the server
     */
    protected void stop() {
        keepGoing = false;
        // connect to myself as Client to exit statement
        // Socket socket = new Socket("localhost", port);
        try {
            new Socket("localhost", port);
        } catch (Exception e) {
            // nothing I can really do
        }
    }

    /*
     * Display an event (not a message)
     */
    private void display(String msg) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date()) + " " + msg;
        System.out.println(time);
    }

    /*
     * Broadcast a message to all Clients
     */
    private synchronized void broadcast(String message) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String messageLf = time + " " + message + "\n";

        System.out.print(messageLf);
        System.out.flush(); // make sure out is flushed
        for (int i = al.size(); i-- > 0;) {
            ClientThread ct = al.get(i);
            if (!ct.writeMsg(messageLf)) {
                al.remove(i);
                display("Disconnected Client " + ct.id + " removed from list.");
            }
        }
    }

    /*
     * For the GUI to broadcast a message
     */
    protected void sendMessage(String message) {
        broadcast("SERVER MESSAGE> " + message);
    }

    /*
     * Remove a client from the ArrayList
     */
    synchronized void remove(int id) {
        for (int i = 0; i < al.size(); ++i) {
            ClientThread ct = al.get(i);
            if (ct.id == id) {
                al.remove(i);
                return;
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5001);
        server.start();
    }

    /**
     * One instance of this thread will run for each client
     */
    class ClientThread extends Thread {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;
        String date;

        // Constructor
        ClientThread(Socket socket) {
            // a unique id
            id = ++uniqueId;
            this.socket = socket;
            System.out.println("Thread trying to create Object Input/Output Streams");
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
                display(username + " just connected.");
            } catch (IOException e) {
                System.out.println("Exception creating new Input/output Streams: " + e);
                return;
            } catch (ClassNotFoundException e) {
                System.out.println(e);
            }
        }

        // what will run forever
        public void run() {
            // to loop until LOGOUT
            boolean keepGoing = true;
            while (keepGoing) {
                // read a message from the client
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException e) {
                    display(username + " disconnected with error reading Streams: " + e);
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }
                // the messaage part of the ChatMessage
                String message = cm.getMessage();

                // Switch on the type of message receive
                switch (cm.getType()) {

                    case ChatMessage.MESSAGE:
                        broadcast(username + ": " + message);
                        break;
                    case ChatMessage.LOGOUT:
                        display(username + " disconnected with LOGOUT.");
                        keepGoing = false;
                        break;
                    case ChatMessage.WHOISIN:
                        writeMsg("List of the users connected at " + sdf.format(new Date())
                                + "\n");
                        for (int i = 0; i < al.size(); ++i) {
                            ClientThread ct = al.get(i);
                            writeMsg((i + 1) + ") " + ct.username + " since "
                                    + ct.date + "\n");
                        }
                        break;
                }
            }
            remove(id);
            close();
        }

        // try to write a string to the Client
        private synchronized boolean writeMsg(String msg) {
            // if Client is still connected send the message to it
            if (!socket.isConnected()) {
                close();
                return false;
            }
            // write the message to the stream
            try {
                sOutput.writeObject(msg);
            }
            // if an error occurs, do not abort just inform the user
            catch (IOException e) {
                System.out.println("Error sending message to " + username);
                System.out.println(e.toString());
            }
            return true;
        }

        // close the connection with the client
        private void close() {
            try {
                if (sOutput != null)
                    sOutput.close();
            } catch (Exception e) {
            }
            try {
                if (sInput != null)
                    sInput.close();
            } catch (Exception e) {
            }
            try {
                if (socket != null)
                    socket.close();
            } catch (Exception e) {
            }
        }

        private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        {
            date = sdf.format(new Date());
        }
    }
}
