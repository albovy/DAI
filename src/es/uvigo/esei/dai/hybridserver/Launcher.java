package es.uvigo.esei.dai.hybridserver;

import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

import java.io.*;

import java.util.Properties;

public class Launcher {
    public static void main(String[] args) throws Exception {
        HybridServer server;
        HTTPResponse response = new HTTPResponse();
        if (args.length == 0) {
            server = new HybridServer();
            server.start();
        } else if (args.length == 1) {
            Configuration conf = new XMLConfigurationLoader().load((new File(args[0])));

            if (conf == null) {
                System.err.println("en el xml hay un error");
                response.setStatus(HTTPResponseStatus.S404);
            }
            server = new HybridServer(conf);

            server.start();
        }
    }
}

