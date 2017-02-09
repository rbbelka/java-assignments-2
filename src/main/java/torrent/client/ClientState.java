package torrent.client;

import torrent.impl.Constants;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ClientState {

    private Map<Integer, BitSet> availableFileParts;
    private Map<Integer, Path> filesPaths;
    private final Map<InetAddress, List<Integer>> toDownloadFiles = new HashMap<>();

    public ClientState() {
        availableFileParts = new HashMap<>();
        filesPaths = new HashMap<>();
    }

    public void addWholeFile(int id, Path path) {
        long size = path.toFile().length();
        filesPaths.put(id, path);
        BitSet fileParts = new BitSet();
        for (int i = 0; i < (size + Constants.BLOCK_SIZE - 1) / Constants.BLOCK_SIZE; i++) {
            fileParts.set(i);
        }
        availableFileParts.put(id, fileParts);
    }

    public void addFilePart(int id, int part, Path path) {
        if (!filesPaths.containsKey(id)) {
            filesPaths.put(id, path);
            availableFileParts.put(id, new BitSet());
        }
        availableFileParts.get(id).set(part);
    }

    public void save() throws IOException {
        File file = Constants.CLIENT_SAVE.toFile();
        if (!file.exists()) {
            Files.createFile(Constants.CLIENT_SAVE);
            file = Constants.CLIENT_SAVE.toFile();
        }
        DataOutputStream output = new DataOutputStream(new FileOutputStream(file));

        output.writeInt(availableFileParts.size());
        for (Map.Entry<Integer, BitSet> entry : availableFileParts.entrySet()) {
            output.writeInt(entry.getKey());
            BitSet availableParts = entry.getValue();
            output.writeInt(availableParts.cardinality());
            for (int i = 0; i < availableParts.size(); i++) {
                if (availableParts.get(i)) {
                    output.writeInt(i);
                }
            }
        }

        output.writeInt(filesPaths.size());
        for (Map.Entry<Integer, Path> filePath : filesPaths.entrySet()) {
            output.writeInt(filePath.getKey());
            output.writeUTF(filePath.getValue().toString());
        }

        output.writeInt(toDownloadFiles.size());
        for (Map.Entry<InetAddress, List<Integer>> entry : toDownloadFiles.entrySet()) {
            output.write(entry.getKey().getAddress());
            List<Integer> fileIds = entry.getValue();
            output.writeInt(fileIds.size());
            for (int id : fileIds) {
                output.writeInt(id);
            }
        }
        output.flush();
        output.close();
    }

    public void restore() throws IOException {
        File file = Constants.CLIENT_SAVE.toFile();
        DataInputStream input = new DataInputStream(new FileInputStream(file));

        availableFileParts = new HashMap<>();
        filesPaths = new HashMap<>();

        int availableFilePartsSize = input.readInt();
        for (int i = 0; i < availableFilePartsSize; i++) {
            int id = input.readInt();
            int setSize = input.readInt();
            BitSet availableParts = new BitSet();
            for (int j = 0; j < setSize; j++) {
                availableParts.set(input.readInt());
            }
            availableFileParts.put(id, availableParts);
        }

        int filesPathsSize = input.readInt();
        for (int i = 0; i < filesPathsSize; i++) {
            int id = input.readInt();
            String path = input.readUTF();
            filesPaths.put(id, Paths.get(path));
        }

        int toDownloadFilesSize = input.readInt();
        for (int i = 0; i < toDownloadFilesSize; i++) {
            byte[] ip = new byte[Constants.IP_BYTES];
            input.read(ip, 0, Constants.IP_BYTES);
            int fileIdsListSize = input.readInt();
            List<Integer> fileIdsList = new ArrayList<>();
            for (int j = 0; j < fileIdsListSize; j++) {
                fileIdsList.add(input.readInt());
            }
            toDownloadFiles.put(InetAddress.getByAddress(ip), fileIdsList);
        }

        input.close();
    }

    public List<Integer> getAvailableFileIds() {
        return new ArrayList<>(availableFileParts.keySet());
    }

    public boolean containsFileById(int id) {
        return availableFileParts.containsKey(id);
    }

    public BitSet getAvailableFilePartsById(int id) {
        return availableFileParts.get(id);
    }

    public Path getPathById(int id) {
        return filesPaths.get(id);
    }

    public void addFileToDownload(byte[] ip, int id) throws UnknownHostException {
        InetAddress address;
        address = InetAddress.getByAddress(ip);
        if (!toDownloadFiles.containsKey(address)) {
            toDownloadFiles.put(address, new ArrayList<>());
        }
        toDownloadFiles.get(address).add(id);
    }

    public List<Integer> getToDownloadFilesWithIp(byte[] ip) throws UnknownHostException {
        return toDownloadFiles.get(InetAddress.getByAddress(ip));
    }
}
