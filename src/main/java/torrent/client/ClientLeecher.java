package torrent.client;

import java.io.IOException;
import java.util.List;

public interface ClientLeecher {
    void connect(byte[] ip, int port) throws IOException;

    void disconnect() throws IOException;

    List<Integer> executeStat(int id) throws IOException;

    byte[] executeGet(int id, int part) throws IOException;
}
