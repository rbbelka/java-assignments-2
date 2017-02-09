package torrent.tracker;

import torrent.impl.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

import static torrent.impl.Constants.SERVER_SAVE;

public class TrackerImpl extends AbstractServer {
    private List<FileInfo> fileList;
    private Map<ClientRoute, Set<Integer>> clients;
    private Map<ClientRoute, TimerTask> removeClientTasks;
    private Function<Socket, Runnable> trackerHandlerFactory = (Socket socket) ->
            new TrackerHandler(socket, fileList, clients, removeClientTasks);

    public TrackerImpl() {
        super(Constants.SERVER_PORT);
        fileList = Collections.synchronizedList(new ArrayList<>());
        clients = Collections.synchronizedMap(new HashMap<>());
        removeClientTasks = Collections.synchronizedMap(new HashMap<>());
        setHandlerFactory(trackerHandlerFactory);
    }

    public static void main(String[] args) {
        Server server = new TrackerImpl();
        server.start();
    }

    public void save() throws IOException {
        File file = SERVER_SAVE.toFile();
        if (!file.exists()) {
            Files.createFile(SERVER_SAVE);
            file = SERVER_SAVE.toFile();
        }
        DataOutputStream output = new DataOutputStream(new FileOutputStream(file));

        output.writeInt(fileList.size());
        for (FileInfo fileInfo : fileList) {
            fileInfo.write(output);
        }

        output.flush();
        output.close();
    }

    public void restore() throws IOException {
        File file = SERVER_SAVE.toFile();
        DataInputStream input = new DataInputStream(new FileInputStream(file));

        fileList = Collections.synchronizedList(new ArrayList<>());

        int filesListSize = input.readInt();
        for (int i = 0; i < filesListSize; i++) {
            fileList.add(FileInfo.read(input));
        }

        input.close();
        setHandlerFactory(trackerHandlerFactory);
    }
}
