package torrent.impl;

public interface Server extends Runnable {
    void start();

    void stop();
}
