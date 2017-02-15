package ftp;

import ftp.client.Client;
import ftp.client.ClientImpl;
import ftp.client.FileContent;
import ftp.exceptions.FtpException;
import ftp.exceptions.ServerException;
import ftp.util.FileItem;
import ftp.server.ServerImpl;
import ftp.server.Server;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FtpTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private static final int PORT = 8080;
    private static final String HOST = "localhost";
//    private static final String RESOURCES = "src/test/resources/";
//    private static final String TEST_DIR = "test/";
    private static final String EMPTY = "empty";
    private static final String DIR1 = "dir1";
    private static final String FILE1 = "file1";
    private static final String FILE2 = "file2";
    private static final String FILE1NDIR = "fileInDir";
    private static final String RECEIVED = "received";
    private static final List<FileItem> EXPECTED_LIST = Arrays.asList(
            new FileItem(FILE1, false)
            , new FileItem(FILE2, false)
            , new FileItem(DIR1, true)
    );
    private File empty;
    private File file1;

    @Before
    public void SetUp() throws IOException {
        file1 = folder.newFile(FILE1);
        folder.newFile(FILE2);
        folder.newFolder(DIR1);
        folder.newFile(DIR1 + "/" + FILE1NDIR);
        empty = folder.newFolder(DIR1, EMPTY);
        FileUtils.writeStringToFile(file1, "some text");
    }

    @Test
    public void testListEmpty() throws IOException, InterruptedException, FtpException {
        Server server = new ServerImpl(PORT);
        Thread tserver = new Thread(server);
        tserver.start();
        try {
            Client client = new ClientImpl(HOST, PORT);
            client.connect();

            List<FileItem> list = client.executeList(empty.getPath());
            assertEquals(Collections.emptyList(), list);

            client.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.stop();
    }

    @Test
    public void testList() throws IOException, InterruptedException, FtpException {
        Server server = new ServerImpl(PORT + 1);
        Thread tserver = new Thread(server);
        tserver.start();

        try {
            Client client = new ClientImpl(HOST, PORT + 1);
            client.connect();

            List<FileItem> list = client.executeList(folder.getRoot().getPath());
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
    public void testGet() throws IOException, InterruptedException, FtpException {
        Server server = new ServerImpl(PORT + 2);
        Thread tserver = new Thread(server);
        tserver.start();

        try {
            Client client = new ClientImpl(HOST, PORT + 2);
            client.connect();

            FileContent received = client.executeGet(file1.getPath());
            File receivedFile = folder.newFile(RECEIVED);
            FileUtils.writeByteArrayToFile(receivedFile, received.getContent());

            assertEquals("file size differ", file1.length(), receivedFile.length());
            assertTrue("file content differ", FileUtils.contentEquals(file1, receivedFile));

            Files.delete(receivedFile.toPath());
            client.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.stop();
    }


    @Test
    public void testSeveralClients() throws IOException, InterruptedException, FtpException {
        Server server = new ServerImpl(PORT + 3);
        Thread tserver = new Thread(server);
        tserver.start();

        final File expected = new File(FILE1);

        int clientsQuantity = 10;
        for (int i = 0; i < clientsQuantity; i++) {
            Thread thread = new Thread(() -> {
                try {
                    Client client = new ClientImpl(HOST, PORT + 3);
                    client.connect();

                    List<FileItem> list = client.executeList(folder.getRoot().getPath());
                    assertEquals("list sizes doesn't match", EXPECTED_LIST.size(), list.size());
                    assertTrue("file lists are different",
                            Arrays.deepEquals(
                                    EXPECTED_LIST.stream().sorted().toArray(),
                                    list.stream().sorted().toArray()));

                    client.disconnect();
                } catch (IOException | FtpException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
            thread.join();
        }
        server.stop();
    }


    @Test
    public void testListTwoTimes() throws IOException, InterruptedException, FtpException {
        Server server = new ServerImpl(PORT + 4);
        Thread tserver = new Thread(server);
        tserver.start();

        try {
            Client client = new ClientImpl(HOST, PORT + 4);
            client.connect();

            List<FileItem> list = client.executeList(folder.getRoot().getPath());

            List<FileItem> list2 = client.executeList(folder.getRoot().getPath());

            client.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.stop();
    }
}
