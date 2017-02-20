package ftp.client;

import ftp.exceptions.ConnectionException;
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


    /**
     * Creates instance of client which will be connecting
     * to the specified port number on the named host.
     *
     * @param host address of host server
     * @param port port on host server
     */
    public ClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Connects client to host
     *
     * @exception  IOException  if an I/O error occurs in work with socket.
     * @exception ConnectionException if can't connect to given host
     */
    @Override
    public void connect() throws IOException, ConnectionException {
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

    /**
     * Disconnects client from host
     *
     * @exception  IOException  if an I/O error occurs in work with socket.
     * @exception ConnectionException if not connected
     */
    @Override
    public void disconnect() throws IOException, ConnectionException {
        if (socket == null) {
            throw new ConnectionException("Client is not connected");
        }
        socket.close();
        System.out.println("Client disconnected");
    }

    /**
     * Sends list query to host
     *
     * @param path path of folder to list
     *
     * @return list of files or folders in given path on host
     *
     * @exception  IOException  if an I/O error occurs in work with socket or streams.
     * @exception ConnectionException if not connected and can't connect
     */
    @Override
    public List<FileItem> executeList(String path) throws IOException, ConnectionException {
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

    /**
     * Sends get query to host
     *
     * @param path path of file to download
     *
     * @return content of asked file
     *
     * @exception  IOException  if an I/O error occurs in work with socket or streams.
     * @exception ConnectionException if not connected and can't connect
     */
    @Override
    public FileContent executeGet(String path) throws IOException, ConnectionException {
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
