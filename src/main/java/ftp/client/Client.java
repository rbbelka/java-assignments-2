package ftp.client;

import ftp.exceptions.ConnectionException;
import ftp.util.FileItem;

import java.io.IOException;
import java.util.List;

public interface Client {

    void connect() throws IOException, ConnectionException;

    void disconnect() throws IOException, ConnectionException;

    List<FileItem> executeList(String path) throws IOException, ConnectionException;

    FileContent executeGet(String path) throws IOException, ConnectionException;
}