package es.uvigo.esei.dai.hybridserver;

import es.uvigo.esei.dai.hybridserver.controller.DefaultServiceController;
import es.uvigo.esei.dai.hybridserver.controller.ServiceController;
import es.uvigo.esei.dai.hybridserver.dao.ServiceDAO;
import es.uvigo.esei.dai.hybridserver.dao.ServiceDBDAO;
import es.uvigo.esei.dai.hybridserver.dao.ServiceMapDAO;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HybridServer {
    private int service_port = 8888;
    private Thread serverThread;
    private boolean stop;
    private Map<String, String> pages;
    private ExecutorService threadPool;
    private Properties properties;

    final ServiceDAO dao;
    final ServiceController controller;



    public HybridServer() {
        Properties propertiesUserPass= new Properties();
        this.properties = new Properties();
        this.threadPool = Executors.newFixedThreadPool(50);
        this.properties.put("port",Integer.toString(getPort()));
        this.properties.put("db.url","jdbc:mysql://localhost:3306/hstestdb");
        this.properties.put("db.user","hsdb");
        propertiesUserPass.put("user","hsdb");
        this.properties.put("db.password","hsdbpass");
        propertiesUserPass.put("password","hsdbpass");


        //dao = new ServiceMapDAO();
        this.dao = new ServiceDBDAO(properties.getProperty("db.url"),propertiesUserPass);
        this.controller = new DefaultServiceController(dao);

    }

    public void setServicePort(int servicePort) {
        this.service_port = servicePort;
    }

    public HybridServer(Map<String, String> pages) {
        this.threadPool = Executors.newFixedThreadPool(50);
        this.dao = new ServiceMapDAO(pages);
        this.controller = new DefaultServiceController(dao);

    }

    public HybridServer(Properties properties) {
        Properties propertiesUserPass= new Properties();
        this.properties = properties;
        this.threadPool = Executors.newFixedThreadPool(Integer.parseInt(properties.getProperty("numClients")));
        propertiesUserPass.put("user",properties.getProperty("db.user"));
        propertiesUserPass.put("password",properties.getProperty("db.password"));
        this.dao = new ServiceDBDAO(properties.getProperty("db.url"),propertiesUserPass);
        this.controller = new DefaultServiceController(dao);
        this.setServicePort(Integer.parseInt(properties.getProperty("port")));

    }

    public int getPort() {
        return service_port;
    }

    public void start() {
        this.serverThread = new Thread(() -> {
            try (final ServerSocket serverSocket = new ServerSocket(getPort())) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    if (stop) break;
                    threadPool.execute(new ServiceThread(clientSocket,controller));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.stop = false;
        this.serverThread.start();
    }

    public void stop() {
        this.stop = true;

        try (Socket socket = new Socket("localhost", getPort())) {
            // Esta conexión se hace, simplemente, para "despertar" el hilo servidor
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            this.serverThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.serverThread = null;
    }
}
