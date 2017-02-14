package ftp.client;

import ftp.exceptions.ConnectionException;
import ftp.exceptions.FtpException;
import ftp.util.FileItem;
import ftp.util.QueryType;

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
    public void connect() throws IOException, FtpException {
        if (socket != null) {
            throw new ConnectionException("Client already connected");
        }
        int tryCount = 50;
        for (int i = 0; i < tryCount && socket == null; i++) {
            try {
                socket = new Socket(host, port);
            } catch (ConnectException e) {
                if (i == tryCount - 1)
                    throw new ConnectionException("Cannot connect");
                System.out.println("Cannot connect. Trying again");
            }
        }

        System.out.println("Connection established");
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

    }

    @Override
    public void disconnect() throws IOException, FtpException {
        if (socket == null) {
            throw new ConnectionException("Client is not connected");
        }
        socket.close();
        System.out.println("Client disconnected");
    }

    @Override
    public List<FileItem> executeList(String path) throws IOException, FtpException {
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
    public FileContent executeGet(String path) throws IOException, FtpException {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        output.writeInt(QueryType.GET.ordinal());
        output.writeUTF(path);
        output.flush();

        int size = (int) input.readLong();
        byte[] content = new byte [size];
        input.read(content);
        return new FileContent(path, size, content);
    }
}
