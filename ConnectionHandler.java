
package id2212.hangman;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anton
 * 
 * Connection handler for the server. Generates a word for a hangman game and 
 * checks input from client if it matches any of the characters of the word.
 */
public class ConnectionHandler implements Runnable {

    private File file;
    private String word;
    private String[] gameWord;
    private Socket clientSocket;
    private int attempts;
    private boolean gameover;
/**
 * Initiates variables and retrieves a random word from a dictionary.
 * @param clientSocket Socket to receive and send messages to.
 */
    public ConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        file = new File("C:/Users/Anton/Documents/KTH/Applikationer för internet/Hangman/words.txt");
        attempts = 10;
        gameover = false;
        try {
            word = randomWord();
            word = "yes";
            gameWord = new String[word.length()];
            for (int i = 0; i < gameWord.length; i++) {
                gameWord[i] = "-";
            }
            //word = "yes";
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retrieves message from client and checks if it matches the game word.
     * Runs until number of attempts have reached 0 or until correct word was guessed.
     * Sends return message with the current state of the hangman game to client. 
     * Example: Game word="electrode" 
     * Client input: "e"
     * Server answer: "e-e-----e"
     */
    @Override
    public void run() {
        try (BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());) {

            byte[] msg = new byte[4096];

            while (gameover == false) {
                int bytesRead = 0;
                int n;

                while ((n = in.read(msg, bytesRead, 256)) != -1) {
                    bytesRead += n;
                    if (bytesRead == 4096) {
                        break;
                    }
                    if (in.available() == 0) {
                        break;
                    }

                }
                String query = new String(msg).trim();
                if (query.length() > 1) {
                    if (query.equals(word)) {
                        gameover = true;
                        toClient("Correct! The word was '" + query + "' \n Your score: " + attempts);
                        System.exit(1);
                    } else {

                        attempts--;
                        if (attempts == 0) {
                            gameover = true;
                            toClient("Game over, you lose.");
                        }
                    }
                } else {
                    int i = word.indexOf(query);
                    if (i != -1) {
                        while (i != -1) {
                            gameWord[i] = query;
                            i = word.indexOf(query, i + 1);
                        }

                    } else {
                        attempts--;
                        if (attempts == 0) {
                            gameover = true;
                            toClient("Game over, you lose.");
                        }
                    }

                }
                StringBuilder builder = new StringBuilder();
                for (String s : gameWord) {
                    builder.append(s);
                }
                if (builder.toString().equals(word)) {
                    gameover=true;
                    toClient("Correct! The word was '" + word + "' \n Your score: " + attempts);

                } else {
                    builder.append(" Attempts left: ");
                    builder.append(attempts);
                    toClient(builder.toString());
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
/**
 * Send a message to the client.
 * @param msg - Message to send.
 * @throws IOException 
 */
    private void toClient(String msg) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());
        byte[] toClient = msg.getBytes();
        out.write(toClient, 0, toClient.length);
        out.flush();
    }
/**
 * Chooses a random word from a file containing dictionary words.
 * @return - Random word
 * @throws FileNotFoundException 
 */
    private String randomWord() throws FileNotFoundException {
        String result = null;
        Random rand = new Random();
        int n = 0;
        for (Scanner sc = new Scanner(file); sc.hasNext();) {
            ++n;
            String line = sc.nextLine();
            if (rand.nextInt(n) == 0) {
                result = line;
            }
        }
        return result;
    }
}
