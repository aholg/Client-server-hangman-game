package id2212.hangman;

/**
 *
 * @author Anton
 * 
 * Starts a new GUI/client for the hangman game.
 */
public class Client {
    public static void main(String[] args){
        GUI gui=new GUI();
        new Thread(gui).start();
    }
}
