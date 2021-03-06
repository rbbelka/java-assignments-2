package vcs.util;

import vcs.repo.Repository;

import java.io.*;

/**
 * Reads from and writes to init file state of {@link Repository}
 */
public class Serializer {

    public static void serialize(Repository obj, String filename) throws IOException {

        File file = new File(filename);
        if (!file.exists()) {
            if (!file.createNewFile())
                throw new IOException("Serialization file cannot be created");
        }

        FileOutputStream os = new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(os);

        out.writeObject(obj);
        out.flush();
        out.close();
    }

    public static Repository deserialize(String filename) throws IOException, ClassNotFoundException {

        FileInputStream is = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(is);

        Object obj = in.readObject();
        in.close();
        return (Repository) obj;
    }

}
