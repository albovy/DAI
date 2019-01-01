package es.uvigo.esei.dai.hybridserver;

import javax.jws.WebMethod;
import java.util.Set;


@javax.jws.WebService
public interface WebService {

    @WebMethod
    public Set<String> uuidHTML();

    @WebMethod
    public Set<String> uuidXML();

    @WebMethod
    public Set<String> uuidXSD();

    @WebMethod
    public Set<String> uuidXSLT();

    @WebMethod
    public String contentHTML(String uuid);

    @WebMethod
    public String contentXML(String uuid);

    @WebMethod
    public String contentXSD(String uuid);

    @WebMethod
    public String contentXSLT(String uuid);

    @WebMethod
    public String getXSD(String uuid);



}
