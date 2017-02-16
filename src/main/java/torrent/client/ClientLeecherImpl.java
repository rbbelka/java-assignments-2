package torrent.client;

import torrent.impl.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static torrent.impl.Constants.ClientQueryType;

/**
 * Sends queries to another clients
 */
public class ClientLeecherImpl implements ClientLeecher {
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
        if (socket != null) {
            socket.close();
        }
    }

    @Override
    public List<Integer> executeStat(int id) throws IOException {
        output.writeByte(ClientQueryType.STAT.ordinal());
        output.writeInt(id);
        output.flush();
        int size = input.readInt();
        List<Integer> partsList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            partsList.add(input.readInt());
        }
        return partsList;
    }

    @Override
    public byte[] executeGet(int id, int part) throws IOException {
        output.writeByte(ClientQueryType.GET.ordinal());
        output.writeInt(id);
        output.writeInt(part);
        output.flush();
        byte[] buffer = new byte[Constants.BLOCK_SIZE];
        input.readFully(buffer);
        return buffer;
    }
}
