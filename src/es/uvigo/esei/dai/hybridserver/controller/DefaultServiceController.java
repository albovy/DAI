package es.uvigo.esei.dai.hybridserver.controller;

import es.uvigo.esei.dai.hybridserver.dao.ServiceDAO;

import java.util.Set;

public class DefaultServiceController implements ServiceController {
    private final ServiceDAO dao;

    public DefaultServiceController(ServiceDAO dao){
        this.dao = dao;
    }



    @Override
    public void createPage(String uuid, String content) {
        this.dao.createPage(uuid,content);
    }

    @Override
    public String getPage(String uuid) {
        return this.dao.getPage(uuid);
    }

    @Override
    public void deletePage(String uuid) {
        this.dao.deletePage(uuid);
    }

    @Override
    public Set<String> listPages() {
        return this.dao.listPages();
    }

}
