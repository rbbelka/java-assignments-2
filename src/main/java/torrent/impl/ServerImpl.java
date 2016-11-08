package torrent.impl;

import torrent.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static torrent.impl.Constants.*;

public class ServerImpl implements Server, Runnable {

    private final int port = SERVER_PORT;
    private ServerSocket serverSocket;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private boolean active;
    private String FAIL_START_MSG = "Could not listen on port " + port;

    private ServerQueryType[] queryTypes = ServerQueryType.values();
    private Random rand = new SecureRandom();

    private Map<Integer, ServerFile> fileMap = new HashMap<>();
    private Map<ClientRoute, ClientInfo> activeClients = new HashMap<>();

    private File saveFile = new File(SERVER_SAVE);
    private PrintWriter saveWriter = new PrintWriter(new FileOutputStream(saveFile, true));

    public ServerImpl() throws FileNotFoundException {
        load();
    }

    private void load() {
        Scanner scanner;
        try {
            scanner = new Scanner(saveFile);
            while (scanner.hasNext()) {
                ServerFile file = ServerFile.read(scanner);
                fileMap.put(file.id, file);
            }
            scanner.close();
        } catch (FileNotFoundException ignored) {
        }
    }

    @Override
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            active = true;
        } catch (IOException e) {
            System.out.println(FAIL_START_MSG);
        }
    }

    private void listen() throws IOException {
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

    @Override
    public void stop() {
        if (serverSocket == null) {
            throw new RuntimeException("Server already stopped");
        }

        try {
            active = false;
            serverSocket.close();
            executorService.shutdown();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        serverSocket = null;
    }

    private void handleConnection(Socket clientSocket) {
        try {
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            while (!clientSocket.isClosed() && clientSocket.isConnected()) {
                byte queryType = input.readByte();
                ServerQueryType currentType = queryTypes[queryType];
                switch (currentType) {
                    case EXIT:
                        clientSocket.close();
                        break;

                    case LIST:
                        listQuery(output);
                        break;

                    case UPLOAD:
                        uploadQuery(input, output);
                        break;

                    case SOURCES:
                        sourcesQuery(input, output);
                        break;

                    case UPDATE:
                        updateQuery(input, output, clientSocket);
                        break;

                    default:
                        throw new RuntimeException("Wrong query type: " + queryType);
                }
            }
        } catch (EOFException ignored) {
            // client disconnected
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void listQuery(DataOutputStream output) throws IOException {
        output.writeInt(fileMap.size());
        for (ServerFile file : fileMap.values()) {
            file.write(output);
        }
        output.flush();
    }

    private void uploadQuery(DataInputStream input, DataOutputStream output) throws IOException {
        ServerFile newFile = ServerFile.readWithoutId(input);

        int id = fileMap.size();
        while (fileMap.containsKey(id)) {
            id = rand.nextInt();
        }
        newFile.id = id;
        fileMap.put(newFile.id, newFile);

        saveWriter.println(newFile.id + " " + newFile.name + " " + newFile.size);
        saveWriter.flush();
        output.writeInt(newFile.id);
        output.flush();
    }

    private void sourcesQuery(DataInputStream input, DataOutputStream output) throws IOException {
        ServerFile file = fileMap.get(input.readInt());

        ArrayList<ClientRoute> clientsToDelete = new ArrayList<>();
        long currentTime = System.currentTimeMillis();

        file.clients.stream()
                .filter(client ->
                        activeClients.get(client).lastUpdateTime + UPDATE_TIMEOUT < currentTime)
                .forEach(this::deleteClient);

        output.writeInt(file.clients.size());

        for (ClientRoute client : file.clients) {
            client.write(output);
        }
        output.flush();
    }

    private void updateQuery(DataInputStream input, DataOutputStream output, Socket clientSocket) throws IOException {
        byte[] ip = clientSocket.getInetAddress().getAddress();

        ClientRoute newClient = new ClientRoute(ip, port);
        deleteClient(newClient);

        int count = input.readInt();
        ArrayList<Integer> fileIds = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            Integer fileId = input.readInt();
            fileIds.add(fileId);

            if (fileMap.containsKey(fileId)) {
                fileMap.get(fileId).clients.add(newClient);
            } else {
                // fileId not from this server
                clientSocket.close();
                return;
            }
        }
        activeClients.put(newClient, new ClientInfo(fileIds, System.currentTimeMillis()));

        output.writeBoolean(true);
        output.flush();
    }

    private void deleteClient(ClientRoute client) {
        if (activeClients.containsKey(client)) {
            activeClients.get(client)
                    .files.forEach(file ->
                    fileMap.get(file).clients.remove(client));
            activeClients.remove(client);
        }
    }

    @Override
    public void run() {
        start();
    }

    private class ClientInfo {
        private ArrayList<Integer> files = new ArrayList<>();
        private long lastUpdateTime;

        ClientInfo(ArrayList<Integer> files, long lastUpdateTime) {
            this.files.addAll(files);
            this.lastUpdateTime = lastUpdateTime;
        }
    }

    public static class ServerFile {
        private int id;
        private String name;
        private long size;

        public ServerFile(String name, long size) {
            this(0, name, size);
        }

        public ServerFile(int id, String name, long size) {
            this.id = id;
            this.name = name;
            this.size = size;
        }

        public void write(DataOutputStream outputStream) throws IOException {
            outputStream.writeInt(id);
            outputStream.writeUTF(name);
            outputStream.writeLong(size);
        }

        public static ServerFile read(Scanner scanner) {
            return new ServerFile(
                    scanner.nextInt(),
                    scanner.next(),
                    scanner.nextLong());
        }

        public static ServerFile readWithoutId(DataInputStream inputStream) throws IOException {
            return new ServerFile(
                    inputStream.readUTF(),
                    inputStream.readLong());
        }

        private Set<ClientRoute> clients = new HashSet<>();
    }
}
