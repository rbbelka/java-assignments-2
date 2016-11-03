package ftp.impl;

import ftp.Client;
import org.apache.commons.io.input.BoundedInputStream;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientImpl implements Client {

    private final String host;
    private final int port;
    private Socket socket = null;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect() throws IOException {
        socket = new Socket(host, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

    }

    @Override
    public void disconnect() throws IOException {
        output.writeInt(0);
        socket.close();
        socket = null;
        input = null;
        output = null;
    }

    @Override
    public List<FileItem> executeList(String path) throws IOException {
        if (socket == null) {
            connect();
        }
        output.writeInt(QueryType.GET.ordinal());
        output.writeUTF(path);
        output.flush();


        int size = input.readInt();
        List<FileItem> fileItems = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
                fileItems.add(new FileItem(input.readUTF(), input.readBoolean()));
        }
        return fileItems;
    }

    @Override
    public InputStream executeGet(String path) throws IOException {
        if (socket == null) {
            connect();
        }
        output.writeInt(QueryType.GET.ordinal());
        output.writeUTF(path);
        output.flush();

        final long size = input.readLong();
        return new BoundedInputStream(input, size);
    }
}
