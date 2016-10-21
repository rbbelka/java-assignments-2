package ftp.impl;

import ftp.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerImpl implements Server, Runnable {

    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService executorService = Executors.newCachedThreadPool();


    public ServerImpl(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Could not listen on port " + port);
        }

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleConnection(clientSocket));
            }
        } catch (IOException e) {
            stop();
        }
    }

    @Override
    public void stop() {

        if (serverSocket != null) {
            try {
                serverSocket.close();
                executorService.shutdown();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        serverSocket = null;
    }

    private void handleConnection(Socket clientSocket) {
        while (!clientSocket.isClosed()) {
            try (
                    DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {
                int queryType = input.readInt();
                String path = input.readUTF();
                switch (queryType) {
                    case QueryType.DISCONNECT:
                        clientSocket.close();
                        break;

                    case QueryType.LIST:
                        listQuery(path, output);
                        break;

                    case QueryType.GET:
                        getQuery(path, output);
                        break;

                    default:
                        throw new RuntimeException("Wrong query type: " + queryType);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listQuery(String path, DataOutputStream output) throws IOException {
        File[] files = new File(path).listFiles();

        if (files == null) {
            output.writeInt(0);
        } else {
            output.writeInt(files.length);
            for (File file : files) {
                output.writeUTF(file.getName());
                output.writeBoolean(file.isDirectory());
            }
        }
        output.flush();
    }

    private void getQuery(String path, DataOutputStream output) throws IOException {
        File file = new File(path);

        if (!file.exists() || !file.isFile()) {
            output.writeLong(0);
        } else {
            byte[] data = Files.readAllBytes(Paths.get(path));
            output.writeLong(data.length);
            output.write(data);
        }
        output.flush();
    }


    @Override
    public void run() {
        start();
    }
}
