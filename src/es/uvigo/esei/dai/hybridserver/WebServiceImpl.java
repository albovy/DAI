package es.uvigo.esei.dai.hybridserver;

import es.uvigo.esei.dai.hybridserver.dao.*;


import java.util.Set;


@javax.jws.WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.WebService", serviceName = "HybridServerService")
public class WebServiceImpl implements WebService {
    private ServiceDAOForXslt XSLTDAO;
    private ServiceDAO HTMLDAO;
    private ServiceDAO XMLDAO;
    private ServiceDAO XSDDAO;

    public WebServiceImpl(Configuration conf){
        this.XSLTDAO = new ServiceDBDADOXSLT(conf);
        this.XMLDAO = new ServiceDBDAOXML(conf);
        this.HTMLDAO = new ServiceDBDAOHTML(conf);
        this.XSDDAO = new ServiceDBDAOXSD(conf);
    }
    @Override
    public Set<String> uuidHTML() {
        return HTMLDAO.listPages();
    }

    @Override
    public Set<String> uuidXML() {
        return XMLDAO.listPages();
    }

    @Override
    public Set<String> uuidXSD() {
        return XSDDAO.listPages();
    }

    @Override
    public Set<String> uuidXSLT() {
        return XSLTDAO.listPages();
    }

    @Override
    public String contentHTML(String uuid) {
        return HTMLDAO.getPage(uuid);
    }

    @Override
    public String contentXML(String uuid) {
        return XMLDAO.getPage(uuid);
    }

    @Override
    public String contentXSD(String uuid) {
        return XSDDAO.getPage(uuid);
    }

    @Override
    public String contentXSLT(String uuid) {
        return XSLTDAO.getPage(uuid);
    }

    @Override
    public String getXSD(String uuid) {
        return XSLTDAO.getXSD(uuid);
    }
}
