
package id2212.hangman;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Anton
 * Server that provides clients with connection handlers for a hangman game.
 */
public class Server {
        private static final int PORT = 4444;

    /**
     * Starts the hangman server.
     *
     * @param args No command line arguments are used.
     */
    public static void main(String[] args)
    {
        boolean listening = true;
        ServerSocket serverSocket;
        try
        {
            serverSocket = new ServerSocket(PORT);
            while (listening)
            {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ConnectionHandler(clientSocket)).start();
            }
            serverSocket.close();
        } catch (IOException e)
        {
            System.err.println("Could not listen on port: " + PORT);
            System.exit(1);
        }
    }
}
