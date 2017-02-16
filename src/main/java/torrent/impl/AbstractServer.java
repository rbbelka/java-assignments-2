package torrent.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * Handles incoming connections
 */
public abstract class AbstractServer implements Server {

    private final int port;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Function<Socket, Runnable> handlerFactory;

    public AbstractServer(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Failed to create ServerSocket: " + e.getMessage());
            System.exit(1);
        }
        executorService = Executors.newCachedThreadPool();
        executorService.execute(() -> {
            while (true) {
                synchronized (this) {
                    if (serverSocket == null || serverSocket.isClosed()) {
                        System.err.println("Server socket is not available");
                        break;
                    }
                }
                try {
                    Socket clientSocket = serverSocket.accept();
                    executorService.execute(handlerFactory.apply(clientSocket));
                } catch (IOException e) {
                    System.err.println("Exception during accepting client: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public synchronized void stop() {
        if (serverSocket == null) {
            System.err.println("Server already stopped");
            return;
        }
        try {
            serverSocket.close();
            executorService.shutdown();
        } catch (IOException e) {
            System.err.println("Exception during closing ServerSocket: " + e.getMessage());
        }
        serverSocket = null;
    }

    public synchronized int getServerSocketPort() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            return serverSocket.getLocalPort();
        }
        return 0;
    }

    protected void setHandlerFactory(Function<Socket, Runnable> handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    @Override
    public void run() {
        start();
    }
}
