package torrent.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Dataclass keeping info about client
 */

public class ClientRoute {

    private static final int HASH_BASE = 31;

    private final byte[] ip;
    private final int port;

    public ClientRoute(byte[] ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public byte[] getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean equals(Object obj) {
        return obj instanceof ClientRoute
                && ip == ((ClientRoute) obj).ip
                && port == ((ClientRoute) obj).port;
    }

    public int hashCode() {
        return Arrays.hashCode(ip) * HASH_BASE + port;
    }

    public void write(DataOutputStream outputStream) throws IOException {
        outputStream.write(ip);
        outputStream.writeInt(port);
    }

    public static ClientRoute read(DataInputStream inputStream) throws IOException {
        byte[] ip = new byte[Constants.IP_BYTES];
        inputStream.read(ip, 0, Constants.IP_BYTES);
        int port = inputStream.readInt();
        return new ClientRoute(ip, port);
    }

}
