package ftp.server;

import ftp.exceptions.ServerException;

public interface Server extends Runnable {

    void start() throws ServerException;

    void stop() throws ServerException;
}
