package es.uvigo.esei.dai.hybridserver.Provider;

import java.util.Set;

public interface PageProvider {
    public String getPage(String uuid);
    public Set<String> listPages();

}
