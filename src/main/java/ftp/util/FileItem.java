package ftp.util;

/**
 * Dataclass for list query
 */

public class FileItem implements Comparable<FileItem> {

    private String filename;
    private boolean isDirectory;
    private static final int HASH_BASE = 31;

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

    public boolean equals(Object obj) {
        return (obj instanceof FileItem &&
                this.filename.equals(((FileItem) obj).filename) &&
                this.isDirectory == ((FileItem) obj).isDirectory);
    }

    public int hashCode() {
        return filename.hashCode() * HASH_BASE + Boolean.hashCode(isDirectory);
    }

    public int compareTo(FileItem fileItem) {
        int nameCompare = filename.compareTo(fileItem.filename);
        return nameCompare != 0 ? nameCompare : Boolean.compare(isDirectory, fileItem.isDirectory);
    }

}
