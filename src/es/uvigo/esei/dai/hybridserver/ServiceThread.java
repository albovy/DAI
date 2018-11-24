package es.uvigo.esei.dai.hybridserver;


import es.uvigo.esei.dai.hybridserver.controller.ServiceController;
import es.uvigo.esei.dai.hybridserver.http.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import java.util.Set;
import java.util.UUID;


public class ServiceThread implements Runnable {
    private final Socket socket;
    final ServiceController controller;

    public ServiceThread(Socket clientSocket,ServiceController controller) {
        this.socket = clientSocket;
        this.controller = controller;
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
                            String uuid = request.getResourceParameters().get("uuid");
                            String contenidoGet = controller.getPage(uuid);
                            if (contenidoGet != null) {
                                response.setContent(contenidoGet);
                                response.setStatus(HTTPResponseStatus.S200);
                                response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
                            } else {
                                String resourceName = request.getResourceName();
                                if (!resourceName.equals("html") && !resourceName.equals("xml") && !resourceName.equals("xslt")) {
                                    response.setStatus(HTTPResponseStatus.S400);
                                } else {

                                    response.setStatus(HTTPResponseStatus.S404);
                                }
                                response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
                            }

                        } else {
                            String resourceName = request.getResourceName();

                            if (resourceName.equals("html") || resourceName.equals("xml") || resourceName.equals("xslt")) {
                                StringBuilder listadoHtml = new StringBuilder();
                                Set<String> keys = controller.listPages();
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
                    if (contenidoDePost != null) {
                        UUID randomUuid = UUID.randomUUID();
                        String uuid = randomUuid.toString();


                        controller.createPage(uuid, contenidoDePost);

                        String uuidHyperlink = "<a href=\"" + resourceName + "?uuid=" + uuid + "\">" + uuid + "</a>";
                        response.setContent(uuidHyperlink);
                        response.setStatus(HTTPResponseStatus.S200);
                        response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
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

                            if (resourceParameter != null) {
                                controller.deletePage(resourceParameter);
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
}
