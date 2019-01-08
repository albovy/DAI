package es.uvigo.esei.dai.hybridserver.Provider;

import es.uvigo.esei.dai.hybridserver.WebService;
import es.uvigo.esei.dai.hybridserver.controller.RemoteServers;

import java.util.*;

public class HTMLPageProvider implements PageProvider {

    private RemoteServers remote;

    public HTMLPageProvider(RemoteServers remote){
        this.remote = remote;
    }
    @Override
    public Set<String> listPages(){
        Set<String> result = new HashSet<>();
        Iterator<String> it;
        for (WebService webService : this.remote.remotes().values()) {
            it = webService.uuidHTML().iterator();
            while (it.hasNext())
                result.add(it.next());
        }
        return result;

    }

    @Override
    public String getPage(String uuid) {
        String content=null;
        for (WebService webService : this.remote.remotes().values()) {
            content = webService.contentHTML(uuid);
            if (content != null)
                break;
        }
        return content;
    }

}
