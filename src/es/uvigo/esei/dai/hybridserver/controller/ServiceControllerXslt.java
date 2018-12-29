package es.uvigo.esei.dai.hybridserver.controller;

import es.uvigo.esei.dai.hybridserver.dao.ServiceDAO;
import es.uvigo.esei.dai.hybridserver.dao.ServiceDAOForXslt;

import java.util.Set;

public class ServiceControllerXslt implements ServiceControllerForXslt {
    private final ServiceDAOForXslt dao;

    public ServiceControllerXslt(ServiceDAOForXslt dao){
        this.dao = dao;
    }



    @Override
    public void createPage(String uuid, String content, String uuidXslt) {
        this.dao.createPage(uuid,content,uuidXslt);
    }

    @Override
    public String getPage(String uuid) {
        return this.dao.getPage(uuid);
    }

    public String getXSD(String uuid){ return this.dao.getXSD(uuid);}

    @Override
    public void deletePage(String uuid) {
        this.dao.deletePage(uuid);
    }

    @Override
    public Set<String> listPages() {
        return this.dao.listPages();
    }

}
