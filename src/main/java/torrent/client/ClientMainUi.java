package torrent.client;

import torrent.impl.Constants;
import torrent.ui.ClientMainFrame;

import java.io.IOException;

/**
 * Provides UI to client
 */
public class ClientMainUi {
    public static void main(String[] args) {
        Client client = new ClientImpl();
        if (Constants.CLIENT_SAVE.toFile().exists()) {
            try {
                client.restore();
            } catch (IOException e) {
                System.err.println("Can't restore from savefile: " + e.getMessage());
            }
        }

        ClientMainFrame frame;
        try {
            frame = new ClientMainFrame(client);
        } catch (IOException e) {
            System.err.println("Exception during frame initialization: " + e.getMessage());
            return;
        }
        frame.setVisible(true);
    }
}
