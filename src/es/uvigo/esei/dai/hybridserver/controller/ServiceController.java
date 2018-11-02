package es.uvigo.esei.dai.hybridserver.controller;


import java.util.Set;

public interface ServiceController {
    public void createPage(String uuid, String content);
    public String getPage(String uuid);
    public void deletePage(String uuid);
    public Set<String> listPages();
}
