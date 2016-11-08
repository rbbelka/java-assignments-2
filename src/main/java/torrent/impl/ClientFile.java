package torrent.impl;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static torrent.impl.Constants.BLOCK_SIZE;

/**
 * Dataclass used by client
 * File
 */

public class ClientFile implements Serializable {
    private static final int PART_SIZE = BLOCK_SIZE;

    private int id;
    private String name;
    private long size;
    private boolean[] parts;

    public ClientFile(int id, String name, long size) throws FileNotFoundException {
        this.id = id;
        this.name = name;
        this.size = size;
        this.parts = new boolean[partsQuantity(this.size)];

        Arrays.fill(this.parts, false);
    }

    //get info from local file and write it into output
    public ClientFile(String filename, DataInputStream input, DataOutputStream output)
            throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "r");

        this.id = input.readInt();
        this.name = filename;
        this.size = file.length();

        parts = new boolean[partsQuantity(size)];
        Arrays.fill(parts, true);

        output.writeByte(Constants.ServerQueryType.UPLOAD.ordinal());
        output.writeUTF(filename.substring(filename.lastIndexOf('/') + 1));
        output.writeLong(file.length());

        file.close();
    }

    // constructor from savefile
    public ClientFile(DataInputStream input) throws IOException {
        this.id = input.readInt();
        this.name = input.readUTF();
        this.size = input.readLong();

        int count = partsQuantity(this.size);
        this.parts = new boolean[count];
        for (int i = 0; i < count; ++i) {
            this.parts[i] = (Objects.equals(input.readUTF(), "1"));
        }
    }

    // write to savefile
    public void write(DataOutputStream output) throws IOException {
        output.writeInt(id);
        output.writeUTF(name);
        output.writeLong(size);

        for (boolean part : parts) {
            output.writeUTF(part ? "1" : "0");
        }
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

    private static int partsQuantity(long size) {
        return (int) ((size + PART_SIZE - 1) / PART_SIZE);
    }

    public int partLength(int index) {
        return index == parts.length - 1 ? (int) (size % PART_SIZE) : PART_SIZE;
    }

    public long partPosition(int index) {
        return index * ((long) PART_SIZE);
    }

    public boolean hasPart(Integer index) {
        return parts[index];
    }

    public List<Integer> presentParts() {
        return IntStream.range(0, parts.length)
                .filter(this::hasPart)
                .boxed()
                .collect(Collectors.toList());
    }

    public void savePart(byte[] content, int index) throws IOException {
        System.err.println("Save part to file: " + name);
        RandomAccessFile file = new RandomAccessFile(name, "rw");
        file.seek(partPosition(index));
        file.write(content);
        parts[index] = true;
        file.close();
    }

    public boolean sendPart(int index, DataOutputStream output) throws IOException {
        if (index < 0 || index >= parts.length || !parts[index]) {
            return false;
        }

        RandomAccessFile file = new RandomAccessFile(name, "r");
        file.seek(partPosition(index));

        byte[] content = new byte[partLength(index)];
        file.read(content);
        output.write(content);

        file.close();
        return true;
    }

}
