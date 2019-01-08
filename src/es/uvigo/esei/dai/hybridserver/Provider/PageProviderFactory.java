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

    public HTMLPageProvider createHTMLProvider(){
        return new HTMLPageProvider(remote);
    }

    public XMLPageProvider createXMLProvider(){
        return new XMLPageProvider(remote);
    }

    public XSDPageProvider createXSDProvider(){
        return new XSDPageProvider(remote);
    }
    public XSLTPageProvider createXSLTProvider(){
        return new XSLTPageProvider(remote);
    }
}
