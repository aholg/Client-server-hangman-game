
package id2212.hangman;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Anton
 *
 * GUI for a client hangman game.
 */
public class GUI extends JPanel implements Runnable {

    private JButton gameButton;
    private JButton sendWordButton;
    private ServerConnection connection;
    private JLabel resultLabel = new JLabel();

    /**
     * Builds the gui.
     */
    public GUI() {
        buildGui();
    }

    /**
     * Creates the game window.
     */
    @Override
    public void run() {
        JFrame frame = new JFrame("Hangman");
        frame.setContentPane(new GUI());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    /**
     * Adds the components of the game window.
     */
    private void buildGui() {
        setLayout(new GridLayout(3, 1));
        add(createGamePanel());
        add(createWordPanel());
        add(resultLabel);
    }

    /**
     * Creates a panel for the game with a button that starts a connection to
     * the server and a new game.
     *
     * @return a JPanel
     */
    private Component createGamePanel() {
        JPanel gamePanel = new JPanel();
        gamePanel.setBorder(new TitledBorder(new EtchedBorder(), "Hangman"));

        gameButton = new JButton("Start new game");
        gamePanel.add(gameButton);
        gameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                resultLabel.setText("");
                connection
                        = new ServerConnection(GUI.this, "127.0.0.1", 4444);
                new Thread(connection).start();
            }
        }
        );
        return gamePanel;
    }

    /**
     * Creates a JPanel with a label, textinput and a sendbutton. The player can
     * enter a word or a letter for the hangman game.
     *
     * @return
     */
    private Component createWordPanel() {
        JPanel wordPanel = new JPanel();
        wordPanel.setBorder(new TitledBorder(new EtchedBorder(), "Guess"));

        wordPanel.add(new JLabel("Enter word or letter:"));
        final JTextField wordField = new JTextField(10);
        wordPanel.add(wordField);
        sendWordButton = new JButton("Submit");
        sendWordButton.setEnabled(false);
        wordPanel.add(sendWordButton);
        sendWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                connection.guessEntry(wordField.getText());

            }
        }
        );
        return wordPanel;
    }

    /**
     * Enables the send button
     */
    void connected() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                sendWordButton.setEnabled(true);

            }
        });
    }

    /**
     * Sets the result label with the state of the hangman word.
     *
     * @param result
     */
    void showResult(final String result) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                resultLabel.setText(result);
            }
        });
    }

    /**
     * Ends the hangman game.
     */
    void resetState() {
        sendWordButton.setEnabled(false);
    }
}
