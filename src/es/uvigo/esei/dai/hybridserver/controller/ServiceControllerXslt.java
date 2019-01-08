package es.uvigo.esei.dai.hybridserver.controller;

import es.uvigo.esei.dai.hybridserver.Provider.PageProvider;
import es.uvigo.esei.dai.hybridserver.Provider.XSLTPageProvider;

import es.uvigo.esei.dai.hybridserver.dao.ServiceDAOForXslt;

import java.util.Set;

public class ServiceControllerXslt implements ServiceControllerForXslt {
    private final ServiceDAOForXslt dao;
    private final XSLTPageProvider provider;

    public ServiceControllerXslt(ServiceDAOForXslt dao, PageProvider provider){
        this.dao = dao;
        this.provider = (XSLTPageProvider) provider;
    }



    @Override
    public void createPage(String uuid, String content, String uuidXslt) {
        this.dao.createPage(uuid,content,uuidXslt);
    }

    @Override
    public String getPage(String uuid) {
        String content = this.dao.getPage(uuid);
        if(content == null){
            content = provider.getPage(uuid);
        }
        return content;
    }

    public String getXSD(String uuid){
       String content = this.dao.getXSD(uuid);
        if(content == null){
            content = provider.getXSD(uuid);
        }
        return content;

    }

    @Override
    public void deletePage(String uuid) {
        this.dao.deletePage(uuid);
    }

    @Override
    public Set<String> listPages() {
        Set<String> pages = this.dao.listPages();
        pages.addAll(provider.listPages());

        return pages;
    }

}
