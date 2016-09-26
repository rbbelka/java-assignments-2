package vcs.util;

/**
 * @author natalia on 26.09.16.
 */
public class Util {

    public static String currentDir() {
        return System.getProperty("user.dir");
    }

    public static String vcsDir() {
        return currentDir() + "/.vcs";
    }

}
