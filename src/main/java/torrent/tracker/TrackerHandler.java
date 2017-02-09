package torrent.tracker;

import torrent.exceptions.TorrentException;
import torrent.impl.ClientRoute;
import torrent.impl.Constants;
import torrent.impl.FileInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

import static torrent.impl.Constants.ServerQueryType;

public class TrackerHandler implements Runnable {

    private final Socket clientSocket;
    private final List<FileInfo> fileList;
    private final Map<ClientRoute, Set<Integer>> clients;
    private final Map<ClientRoute, TimerTask> removeClientTasks;
    private final Timer removeClientTimer;
    private ServerQueryType[] queryTypes = ServerQueryType.values();

    public TrackerHandler(Socket clientSocket, List<FileInfo> fileList, Map<ClientRoute, Set<Integer>> clientSeededFiles, Map<ClientRoute, TimerTask> toRemoveClientTasks) {
        this.clientSocket = clientSocket;
        this.fileList = fileList;
        this.clients = clientSeededFiles;
        this.removeClientTasks = toRemoveClientTasks;
        removeClientTimer = new Timer();
    }

    @Override
    public void run() {
        while (!clientSocket.isClosed()) {
            DataInputStream input;
            DataOutputStream output;
            try {
                input = new DataInputStream(clientSocket.getInputStream());
                output = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                System.err.println("Failed to get streams from clientSocket: " + e.getMessage());
                return;
            }
            byte queryType;
            try {
                queryType = input.readByte();
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
                        updateQuery(input, output);
                        break;

                    default:
                        throw new TorrentException("Wrong query type: " + queryType);
                }
            } catch (EOFException ignored) {
                // client disconnected
            } catch (IOException | TorrentException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void listQuery(DataOutputStream output) throws IOException {
        output.writeInt(fileList.size());
        synchronized (fileList) {
            for (FileInfo fileInfo : fileList) {
                fileInfo.write(output);
            }
        }
        output.flush();
    }

    private void uploadQuery(DataInputStream input, DataOutputStream output) throws IOException {
        FileInfo fileInfo = FileInfo.readWithoutId(input);

        int id = IdProvider.getInstance().getNextId();
        fileInfo.setId(id);
        fileList.add(fileInfo);
        output.writeInt(id);
        output.flush();
    }

    private void sourcesQuery(DataInputStream input, DataOutputStream output) throws IOException {
        int id = input.readInt();

        List<ClientRoute> seedingClientsList;
        synchronized (clients) {
            seedingClientsList = clients.entrySet().stream()
                    .filter(entry -> entry.getValue().contains(id))
                    .map(Map.Entry::getKey).collect(Collectors.toList());
        }
        output.writeInt(seedingClientsList.size());
        for (ClientRoute clientRoute : seedingClientsList) {
            clientRoute.write(output);
        }
        output.flush();
    }

    private void updateQuery(DataInputStream input, DataOutputStream output) throws IOException {
        try {
            byte[] ip = clientSocket.getInetAddress().getAddress();
            int port = input.readInt();
            ClientRoute clientRoute = new ClientRoute(ip, port);

            synchronized (removeClientTasks) {
                if (removeClientTasks.containsKey(clientRoute)) {
                    removeClientTasks.get(clientRoute).cancel();
                }
            }

            int count = input.readInt();
            Set<Integer> fileIds = new HashSet<>();
            for (int i = 0; i < count; i++) {
                fileIds.add(input.readInt());
            }

            synchronized (clients) {
                clients.put(clientRoute, fileIds);
            }

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    synchronized (clients) {
                        clients.remove(clientRoute);
                    }
                }
            };
            synchronized (removeClientTasks) {
                removeClientTasks.put(clientRoute, task);
                removeClientTimer.schedule(task, Constants.UPDATE_TIMEOUT);
            }
        } catch (IOException e) {
            System.err.println("Exception during handling update request: " + e.getMessage());
            output.writeBoolean(false);
            output.flush();
            return;
        }
        output.writeBoolean(true);
        output.flush();
    }
}
