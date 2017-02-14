package ftp.client;

import ftp.exceptions.FtpException;
import ftp.util.FileItem;

import java.io.IOException;
import java.util.List;

public interface Client {

    void connect() throws IOException, FtpException;

    void disconnect() throws IOException, FtpException;

    List<FileItem> executeList(String path) throws IOException, FtpException;

    FileContent executeGet(String path) throws IOException, FtpException;
}