package torrent.client;

import torrent.exceptions.FileIsNotOnTrackerException;
import torrent.exceptions.TorrentException;
import torrent.impl.ClientRoute;
import torrent.impl.Constants;
import torrent.impl.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static torrent.impl.Constants.BLOCK_SIZE;

public class ClientImpl implements Client {
    private static final String DOWNLOADS_PATH = "downloads";
    private static final long DELAY = 1000;

    private static final String WRONG_ARGUMENTS_NUMBER = "Wrong number of arguments of command ";
    private static final String WRONG_TRACKER_ADDRESS = "Wrong tracker address format: ";

    private final TrackerEmu tracker = new TrackerEmuImpl();
    private final ClientState clientState;
    private final ClientSeeder clientSeeder;
    private final ClientLeecher clientLeecher;
    private Timer updateTimer;
    private TimerTask updateTask;

    public ClientImpl() {
        clientState = new ClientState();
        clientSeeder = new ClientSeeder(clientState);
        clientLeecher = new ClientLeecherImpl();
    }

    @Override
    public void start(byte[] ip) throws IOException {
        tracker.connect(ip, Constants.SERVER_PORT);
        clientSeeder.start();
        updateTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    tracker.executeUpdate(clientSeeder.getServerSocketPort(),
                            clientState.getAvailableFileIds());
                } catch (IOException e) {
                    System.err.println("Client updater failed: " + e.getMessage());
                }
            }
        };
        updateTimer = new Timer();
        updateTimer.schedule(updateTask, 0, Constants.UPDATE_TIMEOUT);
    }

    @Override
    public void stop() throws IOException {
        tracker.disconnect();
        clientLeecher.disconnect();
        clientSeeder.stop();
        updateTask.cancel();
        updateTimer.cancel();
    }

    @Override
    public List<FileInfo> getFilesList() throws IOException {
        return tracker.executeList();
    }

    @Override
    public void addFileToDownload(byte[] ip, int fileId) throws UnknownHostException {
        clientState.addFileToDownload(ip, fileId);
    }

    @Override
    public void upload(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        File file = path.toFile();
        if (!file.exists() || file.isDirectory()) {
            throw new NoSuchFileException(filePath);
        }
        int id = tracker.executeUpload(path.getFileName().toString(), file.length());
        clientState.addWholeFile(id, path);
        tracker.executeUpdate(clientSeeder.getServerSocketPort(), clientState.getAvailableFileIds());
    }

    @Override
    public void run(byte[] ip) throws IOException, TorrentException {
        List<Integer> filesToDownloadList = clientState.getToDownloadFilesWithIp(ip);
        if (filesToDownloadList == null) {
            return;
        }
        for (int id : filesToDownloadList) {
            download(id, Paths.get(DOWNLOADS_PATH));
        }
    }

    @Override
    public void download(int fileId, Path path) throws IOException, TorrentException {
        List<FileInfo> filesList = tracker.executeList();
        FileInfo fileInfo;
        Optional<FileInfo> fileInfoOpt = filesList.stream().filter(file -> file.getId() == fileId).findFirst();
        if (!fileInfoOpt.isPresent()) {
            throw new FileIsNotOnTrackerException("File " + fileId + "  is not found in tracker list");
        } else {
            fileInfo = fileInfoOpt.get();
        }

        if (!path.toFile().exists()) {
            Files.createDirectory(path);
        }
        Path filePath = path.resolve(fileInfo.getName());
        File file = filePath.toFile();
        RandomAccessFile newFile = new RandomAccessFile(file, "rw");
        long fileSize = fileInfo.getSize();
        newFile.setLength(fileSize);

        int partNumber = (int) ((fileSize + BLOCK_SIZE - 1) / BLOCK_SIZE);
        Set<Integer> availableParts = new HashSet<>();
        while (availableParts.size() != partNumber) {
            List<ClientRoute> clientsList = tracker.executeSources(fileId);
            if (clientsList.isEmpty()) {
                System.err.println("No clients with file " + fileId + " is present now");
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    throw new TorrentException(e.getMessage());
                }
            }
            for (ClientRoute clientRoute : clientsList) {
                try {
                    clientLeecher.connect(clientRoute.getIp(), clientRoute.getPort());
                } catch (IOException e) {
                    continue;
                }
                List<Integer> fileParts = clientLeecher.executeStat(fileId);
                for (int part : fileParts) {
                    if (!availableParts.contains(part)) {
                        newFile.seek(part * BLOCK_SIZE);
                        int partSize = BLOCK_SIZE;
                        if (part == partNumber - 1) {
                            partSize = (int) (fileSize % BLOCK_SIZE);
                        }
                        byte[] buffer;
                        try {
                            buffer = clientLeecher.executeGet(fileId, part);
                        } catch (IOException e) {
                            continue;
                        }
                        newFile.write(buffer, 0, partSize);
                        availableParts.add(part);
                        clientState.addFilePart(fileId, part, filePath);
                        tracker.executeUpdate(clientSeeder.getServerSocketPort(),
                                clientState.getAvailableFileIds());
                    }
                }
            }
        }
        newFile.close();
    }

    @Override
    public void save() throws IOException {
        clientState.save();
    }

    @Override
    public void restore() throws IOException {
        clientState.restore();
    }
}
