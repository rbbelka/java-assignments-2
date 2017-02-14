package ftp.client;

import ftp.util.FileItem;

import java.io.IOException;
import java.util.List;

public interface Client {

    void connect() throws IOException;

    void disconnect() throws IOException;

    List<FileItem> executeList(String path) throws IOException;

    FileContent executeGet(String path) throws IOException;
}