package torrent.client;

import torrent.impl.ClientRoute;
import torrent.impl.FileInfo;

import java.io.IOException;
import java.util.List;

public interface TrackerEmu {
    void connect(byte[] ip, int port) throws IOException;

    void disconnect() throws IOException;

    List<FileInfo> executeList() throws IOException;

    int executeUpload(String name, long size) throws IOException;

    List<ClientRoute> executeSources(int id) throws IOException;

    boolean executeUpdate(int port, List<Integer> seededFiles) throws IOException;
}
