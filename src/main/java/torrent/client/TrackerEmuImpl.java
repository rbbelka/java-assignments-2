package torrent.client;

import torrent.impl.ClientRoute;
import torrent.impl.FileInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static torrent.impl.Constants.ServerQueryType;

/**
 * Sends queries from client to tracker and get responses
 */
public class TrackerEmuImpl implements TrackerEmu {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    @Override
    public void connect(byte[] ip, int port) throws IOException {
        socket = new Socket(InetAddress.getByAddress(ip), port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void disconnect() throws IOException {
        socket.close();
    }

    @Override
    public synchronized List<FileInfo> executeList() throws IOException {
        output.writeByte(ServerQueryType.LIST.ordinal());
        output.flush();
        int size = input.readInt();
        List<FileInfo> filesList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            filesList.add(FileInfo.read(input));
        }
        return filesList;
    }

    @Override
    public synchronized int executeUpload(String name, long size) throws IOException {
        output.writeByte(ServerQueryType.UPLOAD.ordinal());
        output.writeUTF(name);
        output.writeLong(size);
        output.flush();
        return input.readInt();
    }

    @Override
    public synchronized List<ClientRoute> executeSources(int id) throws IOException {
        output.writeByte(ServerQueryType.SOURCES.ordinal());
        output.writeInt(id);
        output.flush();
        int size = input.readInt();
        List<ClientRoute> clientsList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            clientsList.add(ClientRoute.read(input));
        }
        return clientsList;
    }

    @Override
    public synchronized boolean executeUpdate(int port, List<Integer> files) throws IOException {
        output.writeByte(ServerQueryType.UPDATE.ordinal());
        output.writeInt(port);
        output.writeInt(files.size());
        for (int id : files) {
            output.writeInt(id);
        }
        output.flush();
        return input.readBoolean();
    }
}
