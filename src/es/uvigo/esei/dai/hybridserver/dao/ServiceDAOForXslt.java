package es.uvigo.esei.dai.hybridserver.dao;

import java.util.Set;

public interface ServiceDAOForXslt {

    public void createPage(String uuid, String content,String uuidXsd);
    public String getPage(String uuid);
    public String getXSD(String uuid);
    public void deletePage(String uuid);
    public Set<String> listPages();

}
