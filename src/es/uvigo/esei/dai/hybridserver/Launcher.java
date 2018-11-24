package es.uvigo.esei.dai.hybridserver;

import java.io.*;

import java.util.Properties;

public class Launcher {
    public static void main(String[] args) {
        HybridServer hybridServer = null;
        if (args.length != 0) {
            if (args.length == 1) {
                try {
                    Properties props = getPropsFromFile(args[0]);
                    hybridServer = new HybridServer(props);
                } catch (Exception e) {
                    System.err.println(e);
                    System.exit(0);
                }
            } else {
                System.err.println("Arguments can just be one.");
                System.exit(0);
            }
        } else {
            hybridServer = new HybridServer();
        }

        hybridServer.start();
    }

    private static Properties getPropsFromFile(String file) {
        Properties props = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(file);

            // Load a properties file
            props.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return props;
    }
}

