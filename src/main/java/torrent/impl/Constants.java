package torrent.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Types of queries from client to server
 */

public class Constants {

    public static final int SERVER_PORT = 8081;
    public static final int IP_BYTES = 4;

    public static final int UPDATE_INTERVAL = 1000;
    public static final long UPDATE_TIMEOUT = 60 * 1000;
    public static final int BLOCK_SIZE = 10 * 1024 * 1024;

    public static final Path SERVER_SAVE = Paths.get(".torrentServerSave");
    public static final Path CLIENT_SAVE = Paths.get(".torrentClientSave");

    public static final String DOWNLOAD_DIR = "downloads";

    public enum ServerQueryType {
        EXIT,
        LIST,
        UPLOAD,
        SOURCES,
        UPDATE
    }

    public enum ClientQueryType {
        EXIT,
        STAT,
        GET
    }

}