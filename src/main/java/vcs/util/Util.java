package vcs.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

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

    public static void copyFileToDir(String filename, String dir) throws IOException {
        File file = new File(userDir(), filename);
        if (file.exists()) {

            File hashFile = new File(dir, filename + hashSuffix);
            FileUtils.writeStringToFile(hashFile, getMD5(file));

            File newFile = new File(dir, filename);
            FileUtils.copyFile(file, newFile);
        }
    }

}
