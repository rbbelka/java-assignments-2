package ftp.client;

import ftp.exceptions.FtpException;
import ftp.util.FileItem;
import ftp.util.QueryType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
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
        } catch (FtpException e) {
            System.err.println(e.getMessage());
        }catch (IOException e) {
            System.err.println("Couldn't connect to "+ host + " on port " + port);
            return;
        }
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String[] input = scanner.nextLine().split(" ");
                String command = (input.length > 0) ? input[0] : "";
                String path = (input.length > 1) ? input[1] : "";
                try {
                    switch (QueryType.valueOf(command.toUpperCase())) {
                        case EXIT:
                            client.disconnect();
                            return;
                        case LIST:
                            if (path.isEmpty()) {
                                path = ".";
                            }
                            List<FileItem> files = client.executeList(path);
                            System.out.println("Found " + files.size() + " files");
                            for (FileItem file : files)
                                System.out.println(file.getName() + " " + file.isDirectory());
                            break;
                        case GET:
                            String filename = Paths.get(path).getFileName().toString();
                            File file = new File(dir, filename);
                            try {
                                FileContent result = client.executeGet(path);
                                if (result.getSize() > 0) {
                                    FileUtils.writeByteArrayToFile(file, result.getContent());
                                    System.out.println("Downloaded " + path);
                                } else {
                                    System.out.println("File " + path + " doesn't exist");
                                }
                            } catch (FileAlreadyExistsException e) {
                                System.out.println("File " + path + " already exists");
                            }
                            break;
                        default:
                            usage();
                    }
                } catch (IllegalArgumentException e) {
                    usage();
                } catch (FtpException e) {
                    System.err.println(e.getMessage());
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
