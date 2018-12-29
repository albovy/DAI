package es.uvigo.esei.dai.hybridserver;

import es.uvigo.esei.dai.hybridserver.controller.DefaultServiceController;
import es.uvigo.esei.dai.hybridserver.controller.ServiceController;
import es.uvigo.esei.dai.hybridserver.controller.ServiceControllerForXslt;
import es.uvigo.esei.dai.hybridserver.controller.ServiceControllerXslt;
import es.uvigo.esei.dai.hybridserver.dao.*;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class HybridServer {
    private int service_port = 8888;
    private Thread serverThread;
    private boolean stop;
    private ExecutorService threadPool;


    private ServiceDAO daoHTML;
    private ServiceDAO daoXML;
    private ServiceDAO daoXSD;
    private ServiceDAOForXslt daoXSLT;
    private ServiceController controllerHTML;
    private ServiceController controllerXML;
    private ServiceController controllerXSD;
    private ServiceControllerForXslt controllerXSLT;



    public HybridServer() {
        Configuration conf = new Configuration();
        this.threadPool = Executors.newFixedThreadPool(conf.getNumClients());

        //dao = new ServiceMapDAO();
        this.daoHTML = new ServiceDBDAOHTML(conf);
        this.controllerHTML = new DefaultServiceController(daoHTML);
        this.daoXML = new ServiceDBDAOXML(conf);
        this.controllerXML = new DefaultServiceController(daoXML);
        this.daoXSD = new ServiceDBDAOXSD(conf);
        this.controllerXSD = new DefaultServiceController(daoXSD);
        this.daoXSLT = new ServiceDBDADOXSLT(conf);
        this.controllerXSLT = new ServiceControllerXslt(daoXSLT);




    }



    public HybridServer(Map<String, String> pages) {
        this.threadPool = Executors.newFixedThreadPool(50);
        this.daoHTML = new ServiceMapDAO(pages);
        this.controllerHTML = new DefaultServiceController(daoHTML);

    }
    public HybridServer(Configuration configuration) {
        this.threadPool = Executors.newFixedThreadPool(configuration.getNumClients());
        this.service_port = configuration.getHttpPort();
        this.daoHTML = new ServiceDBDAOHTML(configuration);
        this.controllerHTML = new DefaultServiceController(daoHTML);
        this.daoXML = new ServiceDBDAOXML(configuration);
        this.controllerXML = new DefaultServiceController(daoXML);
        this.daoXSD = new ServiceDBDAOXSD(configuration);
        this.controllerXSD = new DefaultServiceController(daoXSD);
        this.daoXSLT = new ServiceDBDADOXSLT(configuration);
        this.controllerXSLT = new ServiceControllerXslt(daoXSLT);

    }

    public HybridServer(Properties properties) {
        Configuration conf = new Configuration();
        conf.setNumClients(Integer.parseInt(properties.getProperty("numClients")));
        conf.setHttpPort(Integer.parseInt(properties.getProperty("port")));
        conf.setDbUser(properties.getProperty("db.user"));
        conf.setDbPassword(properties.getProperty("db.password"));
        conf.setDbURL(properties.getProperty("db.url"));
        this.threadPool = Executors.newFixedThreadPool(Integer.parseInt(properties.getProperty("numClients")));

        this.daoHTML = new ServiceDBDAOHTML(conf);
        this.controllerHTML = new DefaultServiceController(daoHTML);
        this.daoXML = new ServiceDBDAOXML(conf);
        this.controllerXML = new DefaultServiceController(daoXML);
        this.daoXSD = new ServiceDBDAOXSD(conf);
        this.controllerXSD = new DefaultServiceController(daoXSD);
        this.daoXSLT = new ServiceDBDADOXSLT(conf);
        this.controllerXSLT = new ServiceControllerXslt(daoXSLT);

        this.setServicePort(Integer.parseInt(properties.getProperty("port")));

    }

    public int getPort() {
        return service_port;
    }
    public void setServicePort(int servicePort) {
        this.service_port = servicePort;
    }

    public void start() {
        this.serverThread = new Thread(() -> {
            try (final ServerSocket serverSocket = new ServerSocket(getPort())) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    if (stop) break;
                    threadPool.execute(new ServiceThread(clientSocket, controllerHTML, controllerXML, controllerXSD, controllerXSLT));
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
        threadPool.shutdownNow();

        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
