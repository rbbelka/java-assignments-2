package torrent.client;

import torrent.impl.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.BitSet;

import static torrent.impl.Constants.BLOCK_SIZE;
import static torrent.impl.Constants.ClientQueryType;

/**
 * Handles queries from other clients
 */
public class ClientSeederHandler implements Runnable {

    private final Socket socket;
    private final ClientState clientState;

    private ClientQueryType[] queryTypes = ClientQueryType.values();

    public ClientSeederHandler(Socket socket, ClientState clientState) {
        this.socket = socket;
        this.clientState = clientState;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            DataOutputStream output;
            DataInputStream input;
            try {
                output = new DataOutputStream(socket.getOutputStream());
                input = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.err.println("Failed to get streams from socket: " + e.getMessage());
                return;
            }
            int queryType;
            try {
                try {
                    queryType = input.readByte();
                } catch (EOFException ignored) {
                    return;
                }
                Constants.ClientQueryType currentType = queryTypes[queryType];
                switch (currentType) {
                    case STAT:
                        handleStat(input, output);
                        break;
                    case GET:
                        handleGet(input, output);
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void handleStat(DataInputStream input, DataOutputStream output) throws IOException {
        int id = input.readInt();
        if (!clientState.containsFileById(id)) {
            output.writeInt(0);
        } else {
            BitSet availableParts = clientState.getAvailableFilePartsById(id);
            output.writeInt(availableParts.cardinality());
            for (int i = 0; i < availableParts.size(); i++) {
                if (availableParts.get(i)) {
                    output.writeInt(i);
                }
            }
        }
        output.flush();
    }

    private void handleGet(DataInputStream input, DataOutputStream output) throws IOException {
        int id = input.readInt();
        int partNumber = input.readInt();
        if (clientState.containsFileById(id) && clientState.getAvailableFilePartsById(id).get(partNumber)) {
            byte[] buffer = new byte[BLOCK_SIZE];
            DataInputStream fileInputStream = new DataInputStream(
                    Files.newInputStream(clientState.getPathById(id)));
            fileInputStream.skipBytes(partNumber * BLOCK_SIZE);
            try {
                fileInputStream.readFully(buffer);
            } catch (EOFException ignored) {
            }
            fileInputStream.close();
            output.write(buffer);
        }
    }
}
