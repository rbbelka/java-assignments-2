package torrent.impl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static torrent.impl.Constants.CLIENT_SAVE;
import static torrent.impl.Constants.DOWNLOAD_DIR;

public class ClientImpl {

    private final String host;
    private final int port = 0; // get random port in ServerSocket
    private ServerSocket serverSocket;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private Boolean active = true;

    private String FAIL_START_MSG = "Could not startSeeding";
    private String downloadPath;

    private Map<Integer, ClientFile> files = new HashMap<>();

    private File saveFile = new File(CLIENT_SAVE);
    private PrintWriter saveWriter = new PrintWriter(new FileOutputStream(saveFile, true));

    public ClientImpl(String host) throws IOException {
        this.host = host;
        load();
        createDir(DOWNLOAD_DIR);
    }

    private void load() throws IOException {
        try (DataInputStream input = new DataInputStream(new FileInputStream(saveFile))) {
            int count = input.readInt();
            for (int i = 0; i < count; ++i) {
                ClientFile file = new ClientFile(input);
                files.put(file.getId(), file);
            }
        } catch (FileNotFoundException ignored) {
        }
    }

    private void createDir(String path) throws IOException {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            return;
        }
        Files.createDirectory(Paths.get(path));
    }

    public void run() throws IOException, InterruptedException {
        startSeeding();
        executorService.submit(this::startUpdating);
        executorService.submit(this::downloader);
    }

    private void downloader() {
        while (active) {
            //download requested & non completed files
        }
    }

    public void requestDownload(int id) throws FileNotFoundException {
        // add file to download queue
    }

    public void stop() throws IOException {
        active = false;
        save();
        serverSocket.close();
    }

    private void save() {
        // save state of client
    }

    private ArrayList<ClientFile> sendListQuery() {
        return null;
    }

    public void startSeeding() {
        try {
            serverSocket = new ServerSocket(port);
            active = true;
            executorService.submit(this::listen);
        } catch (IOException e) {
            System.out.println(FAIL_START_MSG);
        }
    }

    private void listen() {
        while (active) {
            try {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleConnection(clientSocket));
            } catch (SocketException e) {
                if (active) {
                    System.err.println(e.getMessage());
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }


    public void startUpdating() {}

    private void handleConnection(Socket clientSocket) {    }

}
