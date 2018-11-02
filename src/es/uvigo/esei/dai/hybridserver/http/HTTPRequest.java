package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HTTPRequest {
    private HTTPRequestMethod method;
    private String resourceChain;
    private String[] resourcePath;
    private String resourceName;
    private Map<String, String> resourceParameters;
    private String httpVersion;
    private Map<String, String> headerParameters;
    private String content;

    public HTTPRequest(Reader reader) throws HTTPParseException, IOException {

        resourceChain = "";
        resourcePath = new String[0];
        resourceName = "";
        resourceParameters = new LinkedHashMap<>();
        httpVersion = "";
        headerParameters = new LinkedHashMap<>();


        BufferedReader input = new BufferedReader(reader);
            String line;

            while ((line = input.readLine()) != null && !line.equals("")) {
                if (Pattern.compile("(HEAD|GET|POST|PUT|DELETE|TRACE|OPTIONS|CONNECT)\\s.+\\sHTTP\\/1.1").matcher(line).find()) {

                    String[] resource = line.split(" ");
                    for (HTTPRequestMethod m : HTTPRequestMethod.values()) {
                        if (resource[0].equals(m.toString())) method = m;
                    }

                    resourceChain = resource[1];

                    if (!resourceChain.equals("/"))
                        resourcePath = resourceChain.split("\\?")[0].substring(1).split("/");

                    resourceName = resourceChain.split("\\?")[0].substring(1);

                    if (resourceChain.split("\\?").length > 1) {
                        String[] paramers = resourceChain.split("\\?")[1].split("&");

                        for (String p : paramers)
                            resourceParameters.put(p.split("=")[0], p.split("=")[1]);
                    }

                    httpVersion = resource[2];

                } else if (Pattern.compile(":\\s").matcher(line).find()) {
                    headerParameters.put(line.split(": ")[0], line.split(": ")[1]);
                }
            }

            if (method == null || resourceChain.equals("") || httpVersion.equals("") || line == null)
                throw new HTTPParseException();

            if (headerParameters.get(HTTPHeaders.CONTENT_LENGTH.getHeader()) != null && !headerParameters.get(HTTPHeaders.CONTENT_LENGTH.getHeader()).equals("0")) {
                char[] contentLengthBuffer = new char[Integer.parseInt(headerParameters.get(HTTPHeaders.CONTENT_LENGTH.getHeader()))];
                input.read(contentLengthBuffer, 0, Integer.parseInt(headerParameters.get(HTTPHeaders.CONTENT_LENGTH.getHeader())));
                StringBuilder contentBuilder = new StringBuilder();

                for (char c : contentLengthBuffer) contentBuilder.append(c);

                String contentStr = contentBuilder.toString();

                if (method.equals(HTTPRequestMethod.POST)) {
                    if (headerParameters.get(HTTPHeaders.CONTENT_TYPE.getHeader()) != null &&
                            headerParameters.get(HTTPHeaders.CONTENT_TYPE.getHeader()).contains(MIME.FORM.getMime()))
                        contentStr = URLDecoder.decode(contentStr, "UTF-8");

                    String[] postParamerss = contentStr.split("&");
                    for (String param : postParamerss)
                        resourceParameters.put(param.split("=")[0], param.split("=")[1]);
                }

                content = contentStr;
            }




    }

    public HTTPRequestMethod getMethod() {
        return this.method;
    }

    public String getResourceChain() {
        return this.resourceChain;
    }

    public String[] getResourcePath() {
        return this.resourcePath;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Map<String, String> getResourceParameters() {
        return resourceParameters;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaderParameters() {
        return headerParameters;
    }

    public String getContent() {
        return content;
    }

    public int getContentLength() {
        if (this.content == null) {
            return 0;
        } else {
            return Integer.parseInt(this.headerParameters.get("Content-Length"));
        }
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
                .append(' ').append(this.getHttpVersion()).append("\r\n");

        for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
            sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
        }

        if (this.getContentLength() > 0) {
            sb.append("\r\n").append(this.getContent());
        }

        return sb.toString();
    }
}
