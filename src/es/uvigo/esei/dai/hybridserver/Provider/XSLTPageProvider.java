package es.uvigo.esei.dai.hybridserver.Provider;

import es.uvigo.esei.dai.hybridserver.WebService;
import es.uvigo.esei.dai.hybridserver.controller.RemoteServers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class XSLTPageProvider implements PageProvider {
    private RemoteServers remote;

    public XSLTPageProvider(RemoteServers remote) {
        this.remote = remote;
    }

    @Override
    public String getPage(String uuid) {
        String content=null;
        for (WebService webService : this.remote.remotes().values()) {
            content = webService.contentXSLT(uuid);
            if (content != null)
                break;
        }
        return content;
    }

    @Override
    public Set<String> listPages() {
        Set<String> result = new HashSet<>();
        Iterator<String> it;
        for (WebService webService : this.remote.remotes().values()) {
            it = webService.uuidXSLT().iterator();
            while (it.hasNext())
                result.add(it.next());
        }
        return result;
    }

    public String getXSD(String uuid){
        String content=null;
        for (WebService webService : this.remote.remotes().values()) {
            content = webService.getXSD(uuid);
            if (content != null)
                break;
        }
        return content;
    }
}
