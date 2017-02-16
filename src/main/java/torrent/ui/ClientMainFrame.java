package torrent.ui;

import torrent.client.Client;
import torrent.client.Settings;
import torrent.impl.Constants;
import torrent.impl.FileInfo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClientMainFrame extends JFrame {
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 500;
    private static final String FRAME_NAME = "Torrent";

    private TorrentsTable table;
    private TimerTask tableUpdateTask;

    public ClientMainFrame(Client client) throws IOException {
        super(FRAME_NAME);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                try {
                    client.stop();
                    client.save();
                } catch (IOException e) {
                    System.err.println("Save request exception: " + e.getMessage());
                }
            }
        });
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        client.start(Settings.getIp());
        buildMenuBar(client);
        table = new TorrentsTable(client);
        add(new JScrollPane(table));

        tableUpdateTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    List<FileInfo> filesList = client.getFilesList();
                    table.setFilesList(filesList);
                    table.revalidate();
                    table.repaint();
                } catch (IOException e) {
                    System.err.println("Exception during receiving files list from server: " + e.getMessage());
                }
            }
        };

        Timer tableUpdateTimer = new Timer();
        tableUpdateTimer.schedule(tableUpdateTask, 0, Constants.UPDATE_INTERVAL);

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    private void buildMenuBar(Client client) throws IOException {
        JMenu fileMenu = new JMenu("File");
        JMenuItem uploadItem = new JMenuItem("Upload new file");
        uploadItem.addActionListener((ActionEvent actionEvent) -> uploadFile(client));
        fileMenu.add(uploadItem);

        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem serverIpItem = new JMenuItem("Set server IP");
        serverIpItem.addActionListener((ActionEvent actionEvent) -> setServerIp(client));
        settingsMenu.add(serverIpItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);
    }

    private void uploadFile(Client client) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                client.upload(file.getAbsolutePath());
                tableUpdateTask.run();

            } catch (IOException e) {
                System.err.println("Exception during upload request: " + e.getMessage());
            }
        }
    }

    private void setServerIp(Client client) {
        try {
            client.stop();
            new ServerIpDialog().setVisible(true);
            client.start(Settings.getIp());
            repaint();
        } catch (IOException e) {
            System.err.println("Exception during setting server IP address: " + e.getMessage());
        }
    }
}
