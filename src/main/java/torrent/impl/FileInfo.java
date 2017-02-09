package torrent.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FileInfo {
    private static final int HASH_BASE = 31;

    private int id;
    private final String name;
    private final long size;

    private FileInfo(String name, long size) {
        this(0, name, size);
    }

    public FileInfo(int id, String name, long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public void write(DataOutputStream output) throws IOException {
        output.writeInt(id);
        output.writeUTF(name);
        output.writeLong(size);
    }

    public static FileInfo read(DataInputStream input) throws IOException {
        int id = input.readInt();
        String name = input.readUTF();
        long size = input.readLong();
        return new FileInfo(id, name, size);
    }

    public static FileInfo readWithoutId(DataInputStream input) throws IOException {
        String name = input.readUTF();
        long size = input.readLong();
        return new FileInfo(name, size);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileInfo)) {
            return false;
        }
        FileInfo fileInfo = (FileInfo) obj;
        return id == fileInfo.id
                && name.equals(fileInfo.name)
                && size == fileInfo.size;
    }

    @Override
    public int hashCode() {
        return name.hashCode() * HASH_BASE * HASH_BASE
                + id * HASH_BASE + (int) size;
    }

}
