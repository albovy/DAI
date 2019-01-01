package es.uvigo.esei.dai.hybridserver.controller;

import es.uvigo.esei.dai.hybridserver.Configuration;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.WebService;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RemoteServers {
    private Configuration conf;


    public  RemoteServers(Configuration conf){
        this.conf = conf;
    }

    public Map<ServerConfiguration, WebService> remotes(){
        Map<ServerConfiguration,WebService> services = new HashMap<>();

        for(ServerConfiguration remote : conf.getServers()){
            URL url;
            try{
                url = new URL(remote.getWsdl());

                QName name = new QName(remote.getNamespace(),remote.getService());
                Service service = Service.create(url,name);
                WebService webService = service.getPort(WebService.class);
                services.put(remote,webService);


            }catch (MalformedURLException | WebServiceException e){
                System.out.println("Error con los servicios");
            }
        }
        return services;
    }
}
