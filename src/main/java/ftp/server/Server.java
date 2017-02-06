package ftp.server;

public interface Server extends Runnable {

    void start();

    void stop();
}
