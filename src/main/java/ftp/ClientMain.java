package ftp;

import ftp.impl.ClientImpl;
import ftp.impl.FileItem;
import ftp.impl.QueryType;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Main class to run client
 */

public class ClientMain {

//    private static final String dir = "src/test/resources/downloads";
    private static final String dir = ".";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Host and port should be passed as arguments");
            return;
        }

        String host = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Port number should be integer");
            return;
        }

        Client client = new ClientImpl(host, port);
        try {
            client.connect();
        } catch (IOException e) {
            System.out.println("Couldn't connect to "+ host + " on port " + port);
            return;
        }
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String command = scanner.next();
                try {
                    switch (QueryType.valueOf(command.toUpperCase())) {
                        case EXIT:
                            client.disconnect();
                            return;
                        case LIST:
                            String dirPath = scanner.next();
                            List<FileItem> files = client.executeList(dirPath);
                            System.out.println("Found " + files.size() + " files");
                            for (FileItem file : files)
                                System.out.println(file.getName() + " " + file.isDirectory());
                            break;
                        case GET:
                            String filePath = scanner.next();
                            String filename = Paths.get(filePath).getFileName().toString();
                            File file = new File(dir, filename);
                            try {
                                Files.copy(client.executeGet(filePath), file.toPath());
                                System.out.println("Downloaded " + filePath);
                            } catch (FileAlreadyExistsException e) {
                                System.out.println("File " + filePath + " already exists");
                            }
                            break;
                        default:
                            usage();
                    }
                } catch (IllegalArgumentException e) {
                    usage();
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void usage() {
        System.out.println("Usage:");
        System.out.println("list <path>");
        System.out.println("get <path>");
    }

}
