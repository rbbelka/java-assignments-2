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

    public static String userDir() {
        return System.getProperty("user.dir");
    }

    public static String vcsDir() {
        return userDir() + "/.vcs";
    }

    public static String getInitFile() {
        return vcsDir() + "/init";
    }

    public static String curDir() {
        return vcsDir() + "/current";
    }

    private static String getMD5(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        String md5 = DigestUtils.md5Hex(fis);
        fis.close();
        return md5;
    }

    public static boolean hashEqual(File file1, File file2) throws VcsException {
        try {
            return getMD5(file1).equals(getMD5(file2));
        } catch (IOException e) {
            throw new VcsException(e.getMessage());
        }
    }

    public static void copyFileToDir(String filename, String dir) throws VcsException {
        File file = new File(userDir(), filename);
        if (file.exists()) {
            try {
                File hashFile = new File(dir, filename + hashSuffix);
                FileUtils.writeStringToFile(hashFile, getMD5(file));

                File newFile = new File(dir, filename);
                FileUtils.copyFile(file, newFile);
            } catch (IOException e) {
                throw new VcsException(e.getMessage());
            }
        }
    }

    public static void removeFileFromDir(String filename, String dir) throws VcsException {
        File file = new File(userDir(), filename);
        try {
            Files.deleteIfExists(Paths.get(dir, filename + hashSuffix));
            Files.deleteIfExists(Paths.get(dir, filename));
        } catch (IOException e) {
            throw new VcsException(e.getMessage());
        }
    }

    public static boolean checkFile(String arg) {
        File f = new File(arg);
        if(!f.isFile() || !f.canRead()) {
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
