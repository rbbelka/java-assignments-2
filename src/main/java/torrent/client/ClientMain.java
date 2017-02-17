package torrent.client;

import torrent.exceptions.TorrentException;
import torrent.exceptions.WrongInputException;
import torrent.impl.Constants;
import torrent.impl.FileInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 * Provides cli to client
 */
public class ClientMain {

    public static void main(String[] args) {
        Client client = new ClientImpl();
        if (Constants.CLIENT_SAVE.toFile().exists()) {
            try {
                client.restore();
            } catch (IOException e) {
                System.err.println("Can't restore from savefile: " + e.getMessage());
            }
        }

        byte[] trackerAddress;
        if (args.length < 2) {
            usage();
            return;
        }
        try {
            trackerAddress = InetAddress.getByName(args[1]).getAddress();
        } catch (UnknownHostException e) {
            System.err.println("Host can't be determined by address " + args[1]);
            return;
        }
        try {
            switch (args[0]) {
                case "list":
                    if (args.length != 2) {
                        throw new WrongInputException();
                    }
                    handleList(client, trackerAddress);
                    break;
                case "get":
                    if (args.length != 3) {
                        throw new WrongInputException();
                    }
                    handleGet(client, trackerAddress, Integer.parseInt(args[2]));
                    break;
                case "newfile":
                    if (args.length != 3) {
                        throw new WrongInputException();
                    }
                    handleNewFile(client, trackerAddress, args[2]);
                    break;
                case "run":
                    if (args.length != 2) {
                        throw new WrongInputException();
                    }
                    handleRun(client, trackerAddress);
                    break;
                default:
                    throw new WrongInputException();
            }
        } catch (WrongInputException | NumberFormatException e) {
            usage();
        }
        try {
            client.save();
        } catch (IOException e) {
            System.err.println("Save request exception: " + e.getMessage());
        }
    }

    private static void usage() {
        System.out.println("Usage:");
        System.out.println("list <tracker-ip>");
        System.out.println("get <tracker-ip> <fileId>");
        System.out.println("newfile <tracker-ip> <path>");
        System.out.println("run <tracker-ip>");
    }

    private static void handleList(Client client, byte[] trackerAddress) {
        try {
            client.start(trackerAddress);
            List<FileInfo> filesList = client.getFilesList();
            for (FileInfo fileInfo : filesList) {
                System.out.println("Id: " + fileInfo.getId() + ", " + "Name: " + fileInfo.getName() + ", "
                        + "Size: " + fileInfo.getSize());
            }
            client.stop();
        } catch (IOException e) {
            System.err.println("List request exception: " + e.getMessage());
        }
    }

    private static void handleGet(Client client, byte[] trackerAddress, int id) {
        try {
            client.addFileToDownload(trackerAddress, id);
        } catch (IOException e) {
            System.err.println("Get request exception: " + e.getMessage());
        }
    }

    private static void handleNewFile(Client client, byte[] trackerAddress, String path) {
        try {
            client.start(trackerAddress);
            client.upload(path);
            client.stop();
        } catch (NoSuchFileException e) {
            System.err.println("Wrong file path");
        } catch (IOException e) {
            System.err.println("NewFile request exception: " + e.getMessage());
        }
    }

    private static void handleRun(Client client, byte[] trackerAddress) {
        try {
            client.start(trackerAddress);
            client.run(trackerAddress);
        } catch (NoSuchFileException e) {
            System.err.println("Wrong file path");
        } catch (IOException e) {
            System.err.println("NewFile request exception: " + e.getMessage());
        } catch (TorrentException e) {
            System.err.println(e.getMessage());
        }
    }
}
