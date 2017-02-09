package torrent.client;

import torrent.impl.AbstractServer;

import java.net.Socket;

public class ClientSeeder extends AbstractServer {
    public ClientSeeder(ClientState clientState) {
        super(0);
        setHandlerFactory((Socket socket) -> new ClientSeederHandler(socket, clientState));
    }
}
