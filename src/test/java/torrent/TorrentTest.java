package torrent;


import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import torrent.client.Client;
import torrent.client.ClientImpl;
import torrent.exceptions.TorrentException;
import torrent.impl.Constants;
import torrent.impl.FileInfo;
import torrent.tracker.IdProvider;
import torrent.impl.Server;
import torrent.tracker.TrackerImpl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TorrentTest {


    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private static final String DIR_PATH1 = "dir1";
    private static final String DIR_PATH2 = "dir2";
    private static final String FILE_NAME1 = "file1.txt";
    private static final String FILE_NAME2 = "file2.txt";
    private static final byte[] LOCALHOST = new byte[] {127, 0, 0, 1};
    private static final long DELAY = 1000;


    @Test
    public void testList() throws IOException {
        IdProvider.getInstance().reset();
        Server server = new TrackerImpl();
        server.start();

        Client client1 = new ClientImpl();
        client1.start(LOCALHOST);
        File dir1 = folder.newFolder(DIR_PATH1);
        File file1 = folder.newFile(DIR_PATH1 + "/" + FILE_NAME1);
        FileUtils.writeStringToFile(file1, "some string");
        client1.upload(file1.getPath());

        Client client2 = new ClientImpl();
        client2.start(LOCALHOST);
        List<FileInfo> filesList = client2.getFilesList();
        assertEquals(Collections.singletonList(new FileInfo(0, FILE_NAME1, file1.length())), filesList);

        File dir2 = folder.newFolder(DIR_PATH2);
        File file2 = folder.newFile(DIR_PATH2 + "/" + FILE_NAME2);
        FileUtils.writeStringToFile(file2, "some other string");
        client2.upload(file2.getPath());
        List<FileInfo> filesList2 = client1.getFilesList();
        assertEquals(Arrays.asList(
                new FileInfo(0, FILE_NAME1, file1.length()),
                new FileInfo(1, FILE_NAME2, file2.length())
        ), filesList2);

        client1.stop();
        client2.stop();
        server.stop();
    }

    @Test
    public void testDownload() throws IOException, InterruptedException, TorrentException {
        IdProvider.getInstance().reset();
        Server server = new TrackerImpl();
        server.start();

        Client client1 = new ClientImpl();
        client1.start(LOCALHOST);
        File dir1 = folder.newFolder(DIR_PATH1);
        File file1 = folder.newFile(DIR_PATH1 + "/" + FILE_NAME1);
        FileUtils.writeStringToFile(file1, "test string");
        client1.upload(file1.getPath());

        Client client2 = new ClientImpl();
        client2.start(LOCALHOST);
        File dir2 = folder.newFolder(DIR_PATH2);
        File file2 = new File (folder.getRoot(), DIR_PATH2 + "/" + FILE_NAME1);
        Thread.sleep(DELAY);
        client2.download(0, dir2.toPath());
        assertEquals(file1.length(), file2.length());

        client1.stop();
        client2.stop();
        server.stop();
    }
}
