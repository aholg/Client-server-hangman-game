
package id2212.hangman;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Anton
 *
 * Handles the connection to the server for a client.
 */
public class ServerConnection implements Runnable {

    private final String host;
    private final int port;
    private final GUI gui;
    private BufferedInputStream in;
    private BufferedOutputStream out;

    /**
     * @param gui Client to work with.
     * @param host Server to connect to.
     * @param port Port to use.
     */
    ServerConnection(GUI gui, String host, int port) {
        this.host = host;
        this.port = port;
        this.gui = gui;
    }

    /**
     * Connects to server.
     */
    @Override
    public void run() {
        connect();
    }

    /**
     * Connects to server and informs client if successful.
     */
    private void connect() {
        try {
            Socket clientSocket = new Socket(host, port);
            in = new BufferedInputStream(clientSocket.getInputStream());
            out = new BufferedOutputStream(clientSocket.getOutputStream());
            gui.connected();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host + ".");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: "
                    + host + ".");
            System.exit(1);
        }
    }

    /**
     * Sends a word provided by the client to the server. Retrieves answer from
     * server and informs client about result. Ends the game if the word was
     * correct or attempts ran out.
     *
     * @param entry Entry to send to server.
     */
    void guessEntry(String entry) {
        String result;
        try {
            byte[] toServer = entry.getBytes();
            out.write(toServer, 0, toServer.length);
            out.flush();
            byte[] fromServer = new byte[4096];
            int n = in.read(fromServer, 0, 256);
            result = new String(fromServer).trim();

        } catch (IOException e) {
            result = "Failed to try query, " + e.getMessage();
        }
        gui.showResult(result);
        if (result.contains("Game over") || result.contains("Correct!")) {
            gui.resetState();

        }

    }

}
