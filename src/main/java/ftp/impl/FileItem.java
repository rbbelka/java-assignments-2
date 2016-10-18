package ftp.impl;

/**
 * Dataclass for list query
 */

public class FileItem {

    private String filename;
    private boolean isDirectory;

    public FileItem(String filename, boolean isDirectory) {
        this.filename = filename;
        this.isDirectory = isDirectory;
    }

    public String getName() {
        return filename;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}
