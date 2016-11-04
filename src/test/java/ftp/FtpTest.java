package ftp;

import ftp.impl.ClientImpl;
import ftp.impl.FileItem;
import ftp.impl.ServerImpl;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FtpTest {

    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    private static final String RESOURCES = "src/test/resources/";
    private static final String TEST_DIR = "test/";
    private static final String EMPTY = "empty";
    private static final String DIR1 = "dir1";
    private static final String FILE1 = "file1";
    private static final String FILE2 = "file2";
    private static final String FILE1NDIR = "fileInDir";
    private static final String RECEIVED = "received";
    private static final List<FileItem> EXPECTED_LIST = Arrays.asList(
            new FileItem(FILE1, false)
            , new FileItem(FILE2, false)
            , new FileItem(EMPTY, true)
            , new FileItem(DIR1, true)
    );


    @Test
    public void testListEmpty() throws IOException, InterruptedException {
        Server server = new ServerImpl(PORT);
        Thread tserver = new Thread(server);
        tserver.start();
        try {
            Client client = new ClientImpl(HOST, PORT);
            client.connect();

            List<FileItem> list = client.executeList(RESOURCES + TEST_DIR + EMPTY);
            assertEquals(Collections.emptyList(), list);

            client.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.stop();
    }

    @Test
    public void testList() throws IOException, InterruptedException {
        Server server = new ServerImpl(PORT + 1);
        Thread tserver = new Thread(server);
        tserver.start();

        try {
            Client client = new ClientImpl(HOST, PORT + 1);
            client.connect();

            List<FileItem> list = client.executeList(RESOURCES + TEST_DIR);
            assertEquals("list sizes doesn't match", EXPECTED_LIST.size(), list.size());
            assertTrue("file lists are different",
                    Arrays.deepEquals(
                        EXPECTED_LIST.stream().sorted().toArray(),
                        list.stream().sorted().toArray()));

            client.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.stop();
    }

    @Test
    public void testGet() throws IOException, InterruptedException {
        Server server = new ServerImpl(PORT + 2);
        Thread tserver = new Thread(server);
        tserver.start();

        File expected = new File(RESOURCES + TEST_DIR + FILE1);

        try {
            Client client = new ClientImpl(HOST, PORT + 2);
            client.connect();

            InputStream received = client.executeGet(RESOURCES + TEST_DIR + FILE1);
            File receivedFile = new File(RESOURCES + RECEIVED);
            Files.copy(received, receivedFile.toPath());

            assertEquals("file size differ", expected.length(), receivedFile.length());
            assertTrue("file content differ", FileUtils.contentEquals(expected, receivedFile));

            Files.delete(receivedFile.toPath());
            client.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.stop();
    }


    @Test
    public void testSeveralClients() throws IOException, InterruptedException {
        Server server = new ServerImpl(PORT + 3);
        Thread tserver = new Thread(server);
        tserver.start();

        final File expected = new File(RESOURCES + TEST_DIR + FILE1);

        int clientsQuantity = 10;
        for (int i = 0; i < clientsQuantity; i++) {
            Thread thread = new Thread(() -> {
                try {
                    Client client = new ClientImpl(HOST, PORT + 3);
                    client.connect();

                    List<FileItem> list = client.executeList(RESOURCES + TEST_DIR);
                    assertEquals("list sizes doesn't match", EXPECTED_LIST.size(), list.size());
                    assertTrue("file lists are different",
                            Arrays.deepEquals(
                                    EXPECTED_LIST.stream().sorted().toArray(),
                                    list.stream().sorted().toArray()));

                    client.disconnect();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
            thread.join();
        }
        server.stop();
    }


    @Test
    public void testListTwoTimes() throws IOException, InterruptedException {
        Server server = new ServerImpl(PORT + 1);
        Thread tserver = new Thread(server);
        tserver.start();

        try {
            Client client = new ClientImpl(HOST, PORT + 1);
            client.connect();

            List<FileItem> list = client.executeList(RESOURCES + TEST_DIR);

            List<FileItem> list2 = client.executeList(RESOURCES + TEST_DIR);

            client.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.stop();
    }

}
