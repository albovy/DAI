package es.uvigo.esei.dai.hybridserver.Provider;

import es.uvigo.esei.dai.hybridserver.Configuration;
import es.uvigo.esei.dai.hybridserver.controller.RemoteServers;

public class PageProviderFactory {
    private RemoteServers remote;
    private Configuration conf;

    public PageProviderFactory(Configuration conf){
        this.conf=conf;
        this.remote = new RemoteServers(conf);
    }

    public HTMLPageProviderFactory createHTMLProvider(){
        return new HTMLPageProviderFactory(remote);
    }

    public XMLPageProviderFactory createXMLProvider(){
        return new XMLPageProviderFactory(remote);
    }
}
