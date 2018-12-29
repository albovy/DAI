package es.uvigo.esei.dai.hybridserver;


import es.uvigo.esei.dai.hybridserver.controller.ServiceController;
import es.uvigo.esei.dai.hybridserver.controller.ServiceControllerForXslt;
import es.uvigo.esei.dai.hybridserver.http.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import java.util.Set;
import java.util.UUID;


public class ServiceThread implements Runnable {
    private final Socket socket;
    final ServiceController controllerHTML;
    final ServiceController controllerXML;
    final ServiceController controllerXSD;
    final ServiceControllerForXslt controllerXSLT;


    public ServiceThread(Socket clientSocket,ServiceController controllerHTML, ServiceController controllerXML, ServiceController controllerXSD, ServiceControllerForXslt controllerXSLT) {
        this.socket = clientSocket;
        this.controllerHTML = controllerHTML;
        this.controllerXML = controllerXML;
        this.controllerXSD = controllerXSD;
        this.controllerXSLT = controllerXSLT;
    }


    @Override
    public void run() {
        try (Socket socket = this.socket) {

            Reader in = new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8);

            HTTPRequest request = null;
            try {
                request = new HTTPRequest(in);
            } catch (HTTPParseException e) {
                e.printStackTrace();
            }
            HTTPResponse response = new HTTPResponse();


            if (request.getMethod().equals(HTTPRequestMethod.GET)) {
                if (request.getResourceName().isEmpty()) {
                    response.setContent("<html><body>Hybrid Server</body> Autores : Alejandro Borrajo Viéitez, Iván Sánchez Fernández</html>");
                    response.setStatus(HTTPResponseStatus.S200);
                    response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
                } else {
                    try {
                        if (!request.getResourceParameters().isEmpty()) {
                            String resourceName = request.getResourceName();
                            String uuid = request.getResourceParameters().get("uuid");
                            String contenidoGet;
                            if (resourceName.equals("html") || resourceName.equals("xml") || resourceName.equals("xslt") || resourceName.equals("xsd")) {


                                switch (resourceName) {
                                    case "html":
                                        contenidoGet = controllerHTML.getPage(uuid);
                                        break;
                                    case "xml":
                                        contenidoGet = controllerXML.getPage(uuid);
                                        if(request.getResourceParameters().containsKey("xslt")){
                                            String uuidXSLT = request.getResourceParameters().get("xslt");

                                            if(uuidXSLT!=null){
                                                String uuidXSD = controllerXSLT.getXSD(uuidXSLT);
                                                System.out.println(uuidXSLT);

                                                String contentXSLT = controllerXSLT.getPage(uuidXSLT);

                                                if(uuidXSD != null){
                                                    System.out.println(uuidXSD);
                                                    String contentXSD = controllerXSD.getPage(uuidXSD);
                                                    String contentXML = controllerXML.getPage(uuid);
                                                    System.out.println(contentXSD);
                                                    System.out.println(contentXSLT);
                                                    if(validateXMLSchema(contentXSD,contentXSLT)){
                                                        MIME html = MIME.TEXT_HTML;
                                                        String contenidoTransform = transform(contentXSLT, contentXML);
                                                        response.setContent(contenidoTransform);
                                                        response.putParameter("Content-Type", html.getMime());

                                                        response.setStatus(HTTPResponseStatus.S200);
                                                    }else{

                                                        response.setStatus(HTTPResponseStatus.S400);
                                                    }

                                                }else{
                                                    response.setStatus(HTTPResponseStatus.S404);
                                                }
                                            }else{
                                                response.setStatus(HTTPResponseStatus.S404);
                                            }
                                        }else{
                                            response.setContent(contenidoGet);
                                            response.setStatus(HTTPResponseStatus.S200);
                                            response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());

                                        }

                                        break;
                                    case "xslt":
                                        contenidoGet = controllerXSLT.getPage(uuid);
                                        break;
                                    default:

                                        contenidoGet = controllerXSD.getPage(uuid);
                                        break;
                                }

                                if (contenidoGet != null) {

                                    response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

                                    switch (resourceName) {
                                        case "html":
                                            response.setContent(contenidoGet);
                                            response.setStatus(HTTPResponseStatus.S200);
                                            response.putParameter("Content-Type", MIME.TEXT_HTML.getMime());
                                            break;
                                        case "xml":

                                            break;
                                        case "xslt":
                                            response.setContent(contenidoGet);
                                            response.setStatus(HTTPResponseStatus.S200);
                                            response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());
                                            break;
                                        default:
                                            response.setContent(contenidoGet);
                                            response.setStatus(HTTPResponseStatus.S200);
                                            response.putParameter("Content-Type", MIME.APPLICATION_XML.getMime());
                                            break;
                                    }

                                } else {


                                        response.setStatus(HTTPResponseStatus.S404);

                                    response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
                                }
                            }else{
                                response.setStatus(HTTPResponseStatus.S400);

                                response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
                            }



                        } else {
                            String resourceName = request.getResourceName();

                            if (resourceName.equals("html") || resourceName.equals("xml") || resourceName.equals("xslt") || resourceName.equals("xsd")) {
                                StringBuilder listadoHtml = new StringBuilder();
                                Set<String> keys;


                                switch (resourceName){
                                    case "html": keys = controllerHTML.listPages(); break;
                                    case "xml": keys = controllerXML.listPages(); break;
                                    case "xslt": keys = controllerXSLT.listPages(); break;
                                    default: keys = controllerXSD.listPages(); break;
                                }


                                for (String itUuid : keys) {
                                    listadoHtml.append("<a href=\"");
                                    listadoHtml.append("?uuid=");
                                    listadoHtml.append(itUuid);
                                    listadoHtml.append("\">");
                                    listadoHtml.append(itUuid);
                                    listadoHtml.append("</a>");
                                    listadoHtml.append("<br>");



                                }
                                response.setContent(listadoHtml.toString());
                                response.setStatus(HTTPResponseStatus.S200);
                                response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

                            }
                        }

                    }catch (RuntimeException e){
                        response.setStatus(HTTPResponseStatus.S500);
                        response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
                    }
                }


            } else {
                if (request.getMethod().equals(HTTPRequestMethod.POST)) {

                try {
                    String resourceName = request.getResourceName();
                    String contenidoDePost = request.getResourceParameters().get(resourceName);

                    String xsd = request.getResourceParameters().get("xsd");

                    if (contenidoDePost != null) {
                        UUID randomUuid = UUID.randomUUID();
                        String uuid = randomUuid.toString();
                        String uuidHyperlink;
                        switch (resourceName){
                            case "html": controllerHTML.createPage(uuid, contenidoDePost);
                                uuidHyperlink = "<a href=\"" + resourceName + "?uuid=" + uuid + "\">" + uuid + "</a>";
                                response.setContent(uuidHyperlink);
                                response.setStatus(HTTPResponseStatus.S200);
                                response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

                            break;
                            case "xml": controllerXML.createPage(uuid,contenidoDePost);
                                uuidHyperlink = "<a href=\"" + resourceName + "?uuid=" + uuid + "\">" + uuid + "</a>";
                                response.setContent(uuidHyperlink);
                                response.setStatus(HTTPResponseStatus.S200);
                                response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

                            break;
                            case "xslt":

                                if(xsd==null) {

                                    response.setStatus(HTTPResponseStatus.S400);
                                    response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
                                }else{
                                    if (controllerXSD.getPage(xsd)==null){
                                        response.setStatus(HTTPResponseStatus.S404);
                                        response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
                                    }else{
                                        controllerXSLT.createPage(uuid,contenidoDePost,xsd);
                                        uuidHyperlink = "<a href=\"" + resourceName + "?uuid=" + uuid + "\">" + uuid + "</a>";
                                        response.setContent(uuidHyperlink);
                                        response.setStatus(HTTPResponseStatus.S200);
                                        response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());



                                    }

                                }
                                break;


                            default: controllerXSD.createPage(uuid,contenidoDePost);
                                uuidHyperlink = "<a href=\"" + resourceName + "?uuid=" + uuid + "\">" + uuid + "</a>";
                                response.setContent(uuidHyperlink);
                                response.setStatus(HTTPResponseStatus.S200);
                                response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

                            break;
                        }



                    } else {
                        response.setStatus(HTTPResponseStatus.S400);
                        response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
                    }
                }catch (RuntimeException e){
                    response.setStatus(HTTPResponseStatus.S500);
                    response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
                }


                } else {
                    try {
                        if (request.getMethod().equals(HTTPRequestMethod.DELETE)) {

                            String resourceParameter = request.getResourceParameters().get("uuid");
                            String resourceName = request.getResourceName();


                            if (resourceParameter != null) {
                                switch (resourceName){
                                    case "html":controllerHTML.deletePage(resourceParameter);break;
                                    case "xml":controllerXML.deletePage(resourceParameter);break;
                                    case "xslt":controllerXSLT.deletePage(resourceParameter);break;
                                    default:controllerXSD.deletePage(resourceParameter);



                                    break;
                                }

                                response.setStatus(HTTPResponseStatus.S200);
                            } else {
                                response.setStatus(HTTPResponseStatus.S404);
                            }
                            response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

                        }
                    }catch (RuntimeException e){
                        response.setStatus(HTTPResponseStatus.S500);
                        response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
                    }
                }
            }
            BufferedWriter output = new BufferedWriter(
                    new OutputStreamWriter(
                            new BufferedOutputStream(socket.getOutputStream()), StandardCharsets.UTF_8));
            output.write(response.toString());
            output.flush();
            output.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static boolean validateXMLSchema(String xsd, String xml) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(new StreamSource(new StringReader(xsd)));
            Validator validator = schema.newValidator();
            Source xmlFile = new StreamSource(new StringReader(xml));
            validator.validate(xmlFile);
            return true;

        } catch (Exception e) {
            return false;
        }

    }
    public static String transform(String xslt, String xml) {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transform = null;
        try {
            transform = factory.newTransformer(new StreamSource(new StringReader(xslt)));
        } catch (TransformerConfigurationException e) {

            e.printStackTrace();
        }
        StringWriter writer = new StringWriter();

        try {
            transform.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
        } catch (TransformerException e) {

            e.printStackTrace();
        }

        return writer.toString();
    }
}
