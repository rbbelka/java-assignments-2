package ftp;

import ftp.impl.ClientImpl;
import ftp.impl.FileItem;
import ftp.impl.ServerImpl;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FtpTest {

    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    private static final String RESOURCES = "src/test/resources/";
    private static final String TEST_DIR = "test/";
    private static final String EMPTY = "test/empty/";
    private static final String DIR = "test/dir1/";
    private static final String FILE1 = "test/file1";
    private static final String FILE2 = "test/file2";
    private static final String FILE1NDIR = "test/dir1/fileInDir";
    private static final String RECEIVED = "received";


    @Test
    public void testListEmpty() throws IOException {
        Server server = new ServerImpl(PORT);
        Thread tserver = new Thread(server);
        tserver.start();
        Thread thread = new Thread(() -> {
            try {
                Client client = new ClientImpl(HOST, PORT);
                client.connect();

                List<FileItem> list = client.executeList(RESOURCES + EMPTY);
                assertEquals(Collections.emptyList(), list);

                client.disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        server.stop();
    }

    @Test
    public void testlist() throws IOException {
        Server server = new ServerImpl(PORT + 1);
        Thread tserver = new Thread(server);
        tserver.start();

        List<FileItem> expected = Arrays.asList(
                new FileItem(FILE1, false)
                , new FileItem(FILE2, false)
                , new FileItem(EMPTY, true)
                , new FileItem(DIR, true)
                , new FileItem(FILE1NDIR, false)
        );

        Thread thread = new Thread(() -> {
            try {
                Client client = new ClientImpl(HOST, PORT);
                client.connect();

                List<FileItem> list = client.executeList(RESOURCES + TEST_DIR);
                assertEquals("list sizes doesn't match", expected.size(), list.size());
                assertEquals("file lists are different"
                        , new HashSet<>(expected)
                        , new HashSet<>(list));

                client.disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        server.stop();
    }

    @Test
    public void testGet() throws IOException {
        Server server = new ServerImpl(PORT);
        Thread tserver = new Thread(server);
        tserver.start();

        File expected = new File(RESOURCES + FILE1);

        Thread thread = new Thread(() -> {
            try {
                Client client = new ClientImpl(HOST, PORT);
                client.connect();

                DataInputStream received = (DataInputStream) client.executeGet(RESOURCES + FILE1);

                Long receivedSize = received.readLong();
                assertEquals("file size differ", (Long) expected.length(), receivedSize);

                File receivedFile = new File(RESOURCES + RECEIVED);
                Files.copy(received, receivedFile.toPath());
                assertTrue("file content differ", FileUtils.contentEquals(expected, receivedFile));
                Files.delete(receivedFile.toPath());

                client.disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        server.stop();
    }

}