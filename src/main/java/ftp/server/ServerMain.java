package ftp.server;

import ftp.exceptions.ServerException;

/**
 * Main class to run server
 */

public class ServerMain {


    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Port should be passed as argument");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Port number should be integer");
            return;
        }

        Server server = new ServerImpl(port);
        try {
            server.start();

            server.stop();
        } catch (ServerException e) {
            System.err.println(e.getMessage());
        }

    }

}
