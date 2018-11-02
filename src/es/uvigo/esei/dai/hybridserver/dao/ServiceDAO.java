package es.uvigo.esei.dai.hybridserver.dao;




import java.util.Set;

public interface ServiceDAO {
    public void createPage(String uuid, String content);
    public String getPage(String uuid);
    public void deletePage(String uuid);
    public Set<String> listPages();
}
