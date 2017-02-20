package ftp.client;

/**
 * Keeps content of file, name and size of it
 */
public class FileContent {
    private String filename;
    private long size;
    private byte[] content;

    public FileContent(String filename, long size, byte[] content) {
        this.filename = filename;
        this.size = size;
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public long getSize() {
        return size;
    }

    public byte[] getContent() {
        return content;
    }
}
