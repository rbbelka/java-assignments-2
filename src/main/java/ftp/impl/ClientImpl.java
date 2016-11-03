package ftp.impl;

import ftp.Client;
import org.apache.commons.io.input.BoundedInputStream;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientImpl implements Client {

    private final String host;
    private final int port;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect() throws IOException {
        if (socket != null) {
            throw new RuntimeException("Already connected");
        }
        int tryCount = 50;
        for (int i = 0; i < tryCount && socket == null; i++) {
            try {
                socket = new Socket(host, port);
            } catch (ConnectException e) {
                if (i == tryCount - 1)
                    throw new RuntimeException("Cannot connect");
                System.out.println("Cannot connect. Trying again");
            }
        }

        System.out.println("Connection established");
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

    }

    @Override
    public void disconnect() throws IOException {
        if (socket == null) {
            throw new RuntimeException("not connected");
        }
        socket.close();
        input.close();
        output.close();
    }

    @Override
    public List<FileItem> executeList(String path) throws IOException {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        output.writeInt(QueryType.LIST.ordinal());
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
        if (socket == null || socket.isClosed()) {
            connect();
        }
        output.writeInt(QueryType.GET.ordinal());
        output.writeUTF(path);
        output.flush();

        return new BoundedInputStream(input, input.readLong());
    }
}
