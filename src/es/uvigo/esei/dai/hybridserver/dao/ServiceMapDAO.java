package es.uvigo.esei.dai.hybridserver.dao;



import java.util.*;

public class ServiceMapDAO implements ServiceDAO {
    private Map<String,String> pages;

    public ServiceMapDAO(Map<String,String>pages){
        this.pages = pages;
    }

    @Override
    public void createPage(String uuid, String content) {
        pages.put(uuid, content);


    }

    @Override
    public String getPage(String uuid) {
        return pages.get(uuid);

    }

    @Override
    public void deletePage(String uuid) {
        pages.remove(uuid);

    }

    @Override
    public Set<String> listPages() {
        return pages.keySet();
    }
}
