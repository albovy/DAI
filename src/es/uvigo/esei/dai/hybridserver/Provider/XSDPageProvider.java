package es.uvigo.esei.dai.hybridserver.Provider;

import es.uvigo.esei.dai.hybridserver.WebService;
import es.uvigo.esei.dai.hybridserver.controller.RemoteServers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class XSDPageProvider implements PageProvider {
    private RemoteServers remote;

    public XSDPageProvider(RemoteServers remote) {
        this.remote = remote;
    }

    @Override
    public String getPage(String uuid) {
        String content=null;
        for (WebService webService : this.remote.remotes().values()) {
            content = webService.contentXSD(uuid);
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
            it = webService.uuidXSD().iterator();
            while (it.hasNext())
                result.add(it.next());
        }
        return result;
    }
}
