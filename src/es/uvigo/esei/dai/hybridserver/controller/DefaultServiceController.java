package es.uvigo.esei.dai.hybridserver.controller;

import es.uvigo.esei.dai.hybridserver.Provider.PageProvider;
import es.uvigo.esei.dai.hybridserver.dao.ServiceDAO;

import java.util.Set;

public class DefaultServiceController implements ServiceController {
    private final ServiceDAO dao;
    private PageProvider provider;

    public DefaultServiceController(ServiceDAO dao, PageProvider provider){
        this.dao = dao;
        this.provider = provider;
    }



    @Override
    public void createPage(String uuid, String content) {
        this.dao.createPage(uuid,content);


    }

    @Override
    public String getPage(String uuid) {
        String content = this.dao.getPage(uuid);
        if(content == null){
            content = provider.getPage(uuid);
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
