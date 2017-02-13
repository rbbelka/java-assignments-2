package vcs.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * @author natalia on 26.09.16.
 */
public class Util {

    private static String hashSuffix = "hash";

    public static String getMD5(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return DigestUtils.md5Hex(fis);
        }
    }

    public static boolean hashEqual(File file1, File file2) throws IOException {
        return getMD5(file1).equals(getMD5(file2));
    }

    public static void copyFileAndHashToCurrentDir(String filename, String userDir, String curDir) throws IOException {
        File file = new File(userDir, filename);
        if (file.exists()) {
            File hashFile = new File(curDir, filename + hashSuffix);
            FileUtils.writeStringToFile(hashFile, getMD5(file));

            File newFile = new File(curDir, filename);
            FileUtils.copyFile(file, newFile);
        }
    }

    public static void removeFileAndHashFromCurrentDir(String filename, String userDir, String curDir) throws IOException {
        File file = new File(userDir, filename);
        Files.deleteIfExists(Paths.get(curDir, filename + hashSuffix));
        Files.deleteIfExists(Paths.get(curDir, filename));
    }

    public static boolean checkFile(String arg) {
        File f = new File(arg);
        if (!f.isFile() || !f.canRead()) {
            System.out.println("Incorrect path: " + arg);
            return false;
        }
        return true;
    }

    public static Set<File> listFilesFromDir(String dirname) {
        File dir = new File(dirname);
        Set<File> files = new HashSet<>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                files.addAll(listFilesFromDir(file.getAbsolutePath()));
            else
                files.add(file);
        }
        return files;
    }

}
